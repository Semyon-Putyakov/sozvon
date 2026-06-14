package ru.sozvon.db.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.sozvon.db.dto.*;
import ru.sozvon.db.dto.modelsDTOs.*;
import ru.sozvon.db.kafka.KafkaProducer;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;
import ru.sozvon.db.services.ChatService;
import ru.sozvon.db.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatKafkaConsumer extends BaseKafkaConsumer {
    private final ChatService chatService;
    private final UserService userService;
    private final DtoConverter dtoConverter;

    @Autowired
    public ChatKafkaConsumer(KafkaProducer kafkaProducer, ChatService chatService,
                             UserService userService, ObjectMapper objectMapper, DtoConverter dtoConverter) {
        super(kafkaProducer, objectMapper);
        this.chatService = chatService;
        this.userService = userService;
        this.dtoConverter = dtoConverter;
    }

    /** Обработка сообщений из Kafka топика для операций с чатами **/
    @KafkaListener(topics = "api-db_chat_request", groupId = "db_chat_request_group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String key = record.key();
        String jsonData = record.value();

        String[] parts = splitOperationAndRequestId(key);
        String operation = parts[0];
        String requestId = parts[1];

        try {
            switch (operation) {
                case "getChatById" -> getChatById(jsonData, requestId);
                case "getUserChats" -> getUserChats(jsonData, requestId);
                case "getAllChatMessages" -> getAllChatMessages(jsonData, requestId);
                case "findUserChatsByTitle" -> findUserChatsByTitle(jsonData, requestId);
                case "deleteChat" -> deleteChat(jsonData, requestId);
                case "saveChat" -> saveChat(jsonData, requestId);
                case "addParticipantToChat" -> addParticipantToChat(jsonData, requestId);
                case "removeParticipantFromChat" -> removeParticipantFromChat(jsonData, requestId);
                case "getChatParticipants" -> getChatParticipants(jsonData, requestId);
                default -> sendError("Неизвестная операция: " + operation, requestId);
            }

            ack.acknowledge();
        } catch (Exception e) {
            sendError("Ошибка: " + e.getMessage(), requestId);
        }
    }

    /** Получить чат по ID из JSON данных **/
    private void getChatById(String jsonData, String requestId) throws Exception {
        ChatByIdDto dto = dtoConverter.convertToChatByIdDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        Optional<ChatWithoutMessages> chat = chatService.getChatByIdGetDTO(dto.getChatId());
        if (chat.isEmpty()) {
            sendError("Чат не найден или доступ запрещен", requestId);
        } else {
            sendSuccess("Чат найден", chat.get(), requestId);
        }
    }

    /** Получить все чаты пользователя из JSON данных **/
    private void getUserChats(String jsonData, String requestId) throws Exception {
        IdDto dto = dtoConverter.convertToIdDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        List<ChatWithIdAndTitleDto> chatWithoutMessages = chatService.getUserChatsWithoutMessages(userOpt.get());
        sendSuccess("Чаты пользователя найдены", chatWithoutMessages, requestId);
    }

    /** Получить все сообщения чата из JSON данных **/
    private void getAllChatMessages(String jsonData, String requestId) throws Exception {
        ChatByIdDto dto = dtoConverter.convertToChatByIdDto(jsonData);
        
        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }
        
        Optional<List<Message>> messages = chatService.getAllChatMessages(dto.getChatId(), userOpt.get());
        if (messages.isPresent()) {
            sendSuccess("Сообщения чата найдены", messages.get(), requestId);
        } else {
            sendError("Чат не найден или доступ запрещен", requestId);
        }
    }

    /** Найти чаты пользователя по названию из JSON данных **/
    private void findUserChatsByTitle(String jsonData, String requestId) throws Exception {
        ChatSearchDto dto = dtoConverter.convertToChatSearchDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        Optional<ChatWithIdAndTitleDto> chat = chatService.findUserChatsByTitle(dto.getTitle(), userOpt.get());
        if (chat.isEmpty()) {
            sendError("Чат не найден", requestId);
            return;
        }

        sendSuccess("Чат по поиску найден", chat.get(), requestId);
    }

    /** Удалить чат из JSON данных **/
    private void deleteChat(String jsonData, String requestId) throws Exception {
        ChatByIdDto dto = dtoConverter.convertToChatByIdDto(jsonData);
        
        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }
        chatService.deleteChat(dto.getChatId(), userOpt.get());
        sendSuccess("Чат удален", null, requestId);
    }

    /** Сохранить чат из JSON данных **/
    ///
    private void saveChat(String jsonData, String requestId) throws Exception {
        ChatCreateDto dto = dtoConverter.convertToChatCreateDto(jsonData);
        
        Chat chat = new Chat();
        chat.setTitle(dto.getTitle());
        chat.setIsGroup(dto.getIsGroup());

        List<Integer> list = dto.getParticipantIds();
        List<User> users = new ArrayList<>();
        for (int id: list){
            users.add(userService.getUserById(id).get());
        }

        chat.setParticipants(users);

        chatService.createChat(chat);
        sendSuccess("Чат сохранен", null, requestId);
    }

    /** Добавить участника в чат **/
    private void addParticipantToChat(String jsonData, String requestId) throws Exception {
        ChatParticipantDto dto = dtoConverter.convertToChatParticipantDto(jsonData);
        
        boolean success = chatService.addParticipantToChat(dto.getChatId(), dto.getUserId(), dto.getParticipantId());
        if (success) {
            sendSuccess("Участник добавлен в чат", null, requestId);
        } else {
            sendError("Не удалось добавить участника в чат", requestId);
        }
    }

    /** Удалить участника из чата **/
    private void removeParticipantFromChat(String jsonData, String requestId) throws Exception {
        ChatParticipantDto dto = dtoConverter.convertToChatParticipantDto(jsonData);
        
        boolean success = chatService.removeParticipantFromChat(dto.getChatId(), dto.getUserId(), dto.getParticipantId());
        if (success) {
            sendSuccess("Участник удален из чата", null, requestId);
        } else {
            sendError("Не удалось удалить участника из чата", requestId);
        }
    }

    /** Получить список участников чата **/
    private void getChatParticipants(String jsonData, String requestId) throws Exception {
        IdDto dto = dtoConverter.convertToIdDto(jsonData);
        
        List<UserByUsernameDto> participants = chatService.getChatParticipants(dto.getId());
        sendSuccess("Список участников чата получен", participants, requestId);
    }

    /** Получить топик для ответов **/
    @Override
    protected String getResponseTopic() {
        return "db-api_chat_response";
    }
} 