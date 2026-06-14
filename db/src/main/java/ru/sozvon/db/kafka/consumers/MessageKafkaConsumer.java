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
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;
import ru.sozvon.db.services.ChatService;
import ru.sozvon.db.services.MessageService;
import ru.sozvon.db.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class MessageKafkaConsumer extends BaseKafkaConsumer {
    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;
    private final DtoConverter dtoConverter;

    @Autowired
    public MessageKafkaConsumer(KafkaProducer kafkaProducer, MessageService messageService,
                                ChatService chatService, UserService userService, ObjectMapper objectMapper, DtoConverter dtoConverter) {
        super(kafkaProducer, objectMapper);
        this.messageService = messageService;
        this.chatService = chatService;
        this.userService = userService;
        this.dtoConverter = dtoConverter;
    }

    /** Обработка сообщений из Kafka топика для операций с сообщениями **/
    @KafkaListener(topics = "api-db_message_request", groupId = "db_message_request_group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String key = record.key();
        String jsonData = record.value();

        String[] parts = splitOperationAndRequestId(key);
        String operation = parts[0];
        String requestId = parts[1];

        try {
            switch (operation) {
                case "createMessage" -> createMessage(jsonData, requestId);
                case "editMessage" -> editMessage(jsonData, requestId);
                case "deleteMessage" -> deleteMessage(jsonData, requestId);
                case "getMessagesByChat" -> getMessagesByChat(jsonData, requestId);
                case "getUserMessagesBySender" -> getUserMessagesBySender(jsonData, requestId);
                case "getRepliesToMessage" -> getRepliesToMessage(jsonData, requestId);
                case "searchMessagesByText" -> searchMessagesByText(jsonData, requestId);
                default -> sendError("Неизвестная операция: " + operation, requestId);
            }

            ack.acknowledge();
        } catch (Exception e) {
            sendError("Ошибка: " + e.getMessage(), requestId);
        }
    }


    /** Создать новое сообщение из JSON данных **/
    private void createMessage(String jsonData, String requestId) throws Exception {
        MessageCreateDto dto = dtoConverter.convertToMessageCreateDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getSenderId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        Optional<ChatWithoutMessages> chatOpt = chatService.getChatByIdGetDTO(dto.getChatId());
        if (chatOpt.isEmpty()) {
            sendError("Чат не найден", requestId);
            return;
        }

        if (!isParticipant(chatOpt,userOpt, requestId)) {
            return;
        }

        messageService.createMessage(userOpt, chatOpt, dto);
        sendSuccess("Сообщение создано", null, requestId);
    }

    /** Отредактировать сообщение из JSON данных **/
    private void editMessage(String jsonData, String requestId) throws Exception {
        MessageEditDto dto = dtoConverter.convertToMessageEditDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getSenderId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }
        
        messageService.editMessage(dto, userOpt.get());
        sendSuccess("Сообщение обновлено", null, requestId);
    }

    /** Удалить сообщение из JSON данных **/
    private void deleteMessage(String jsonData, String requestId) throws Exception {
        MessageDeleteDto dto = dtoConverter.convertToMessageDeleteDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        messageService.deleteMessage(dto.getMessageId());
        sendSuccess("Сообщение удалено", null, requestId);
    }

    /** Получить сообщения чата с пагинацией из JSON данных **/
    private void getMessagesByChat(String jsonData, String requestId) throws Exception {
        MessagePaginationDto dto = dtoConverter.convertToMessagePaginationDto(jsonData);
        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }
        
        Optional<ChatWithoutMessages> chatOpt = chatService.getChatByIdGetDTO(dto.getChatId());
        if (chatOpt.isEmpty()) {
            sendError("Чат не найден", requestId);
            return;
        }
        
        if (isParticipant(chatOpt, userOpt, requestId)) {
            List<MessageSendersDto> messages = messageService.getMessagesByChat(chatOpt, userOpt.get(), dto.getPage(), dto.getSize());
            if (messages.isEmpty()) {
                sendError("Сообщения не найдены", requestId);
            } else {
                sendSuccess("Сообщения чата найдены", messages, requestId);
            }
        }
    }

    /** Получить сообщения от определенного отправителя из JSON данных **/
    private void getUserMessagesBySender(String jsonData, String requestId) throws Exception {
        Map<String, Object> data = dtoConverter.convertToMap(jsonData);
        Integer senderId = (Integer) data.get("senderId");
        Integer currentUserId = (Integer) data.get("currentUserId");
        Integer chatId = (Integer) data.get("chatId");

        Optional<User> senderOpt = userService.getUserById(senderId);
        Optional<User> currentUserOpt = userService.getUserById(currentUserId);
        Optional<ChatWithoutMessages> chatOpt = chatService.getChatByIdGetDTO(chatId);

        if (senderOpt.isEmpty() || currentUserOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        if (chatOpt.isEmpty()) {
            sendError("Чат не найден", requestId);
            return;
        }

        if (isParticipant(chatOpt,currentUserOpt, requestId)) {
            List<MessageSendersDto> messages = messageService.getUserMessagesBySender(chatOpt, senderOpt);
            if (messages.isEmpty()) {
                sendError("Сообщения не найдены", requestId);
            } else {
                sendSuccess("Сообщения отправителя найдены", messages, requestId);
            }
        }
    }

    /** Получить ответы на сообщение из JSON данных **/
    private void getRepliesToMessage(String jsonData, String requestId) throws Exception {
        Map<String, Object> data = dtoConverter.convertToMap(jsonData);
        Integer parentMessageId = (Integer) data.get("parentMessageId");
        Integer userId = (Integer) data.get("userId");
        Integer chatId = (Integer) data.get("chatId");

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }

        Optional<ChatWithoutMessages> chatOpt = chatService.getChatByIdGetDTO(chatId);
        if (chatOpt.isEmpty()) {
            sendError("Чат не найден", requestId);
            return;
        }

        Optional<Message> parentMessageOpt = messageService.getMessageById(parentMessageId, userOpt.get());
        if (parentMessageOpt.isEmpty()) {
            sendError("Родительское сообщение не найдено", requestId);
            return;
        }

        if (isParticipant(chatOpt,userOpt, requestId)) {
            Optional<List<MessageRepliesDto>> replies = messageService.getRepliesToMessage(parentMessageOpt.get().getId());
            if (!replies.isEmpty()) {
                sendSuccess("Ответы на сообщение найдены", replies.get(), requestId);
            }
        }
    }

    /** Поиск сообщений по тексту из JSON данных **/
    private void searchMessagesByText(String jsonData, String requestId) throws Exception {
        MessageSearchDto dto = dtoConverter.convertToMessageSearchDto(jsonData);

        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {
            sendError("Пользователь не найден", requestId);
            return;
        }
        Optional<ChatWithoutMessages> chatOpt = chatService.getChatByIdGetDTO(dto.getChatId());
        if (chatOpt.isEmpty()) {
            sendError("Чат не найден не найден", requestId);
            return;
        }

        List<MessageSendersDto> messages = messageService.searchMessagesByText(dto.getText(), chatOpt, userOpt);
        if (messages.isEmpty()) {
            sendError("Сообщения не найдены", requestId);
        }else {
            sendSuccess("Сообщения по поиску найдены", messages, requestId);
        }
    }

    private boolean isParticipant(Optional<ChatWithoutMessages> chatOpt,Optional<User> userOpt, String requestId){
        boolean isParticipant = chatOpt.get().getParticipants().stream()
                .anyMatch(u -> u.getId() == userOpt.get().getId());
        if (!isParticipant) {
            sendError("Пользователь не является участником чата", requestId);
        }
        return isParticipant;
    }

    /** Получить топик для ответов **/
    @Override
    protected String getResponseTopic() {
        return "db-api_message_response";
    }
} 