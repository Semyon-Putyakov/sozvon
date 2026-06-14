package ru.sozvon.db.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.sozvon.db.models.User;
import ru.sozvon.db.services.UserService;

import java.util.List;
import java.util.Optional;
@Service
public class UserKafkaConsumer extends BaseKafkaConsumer {
    private final UserService userService;
    private final DtoConverter dtoConverter;

    @Autowired
    public UserKafkaConsumer(KafkaProducer kafkaProducer, UserService userService, ObjectMapper objectMapper, DtoConverter dtoConverter) {
        super(kafkaProducer, objectMapper);
        this.userService = userService;
        this.dtoConverter = dtoConverter;
    }

    /** Обработка сообщений из Kafka топика для операций с пользователями **/
    @KafkaListener(topics = "api-db_user_request", groupId = "db_user_request_group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String key = record.key();
        String jsonData = record.value();

        String[] parts = splitOperationAndRequestId(key);
        String operation = parts[0];
        String requestId = parts[1];

        try {
            switch (operation) {
                case "getUserById" -> getUserById(jsonData, requestId);
                case "getUserByUsername" -> getUserByUsername(jsonData, requestId);
                case "getUserByLogin" -> getUserByLogin(jsonData, requestId);
                case "createUser" -> createUser(jsonData, requestId);
                case "updateUser" -> updateUser(jsonData, requestId);
                case "deleteUser" -> deleteUser(jsonData, requestId);
                case "getUserChats" -> getUserChats(jsonData, requestId);
                default -> sendError("Неизвестная операция: " + operation, requestId);
            }

            ack.acknowledge();
        } catch (Exception e) {
            sendError("Ошибка: " + e.getMessage(),requestId);
        }
    }

    /** Получить пользователя по ID из JSON данных **/
    private void getUserById(String jsonData,String requestId) throws Exception {
        Optional<User> user = userService.getUserById(dtoConverter.convertToIdDto(jsonData).getId());
        if (user.isPresent()) {
            sendSuccess("Пользователь найден", mapUserToUserFullDto(user.get()), requestId);
        } else {
            sendError("Пользователь не найден", requestId);
        }
    }

    /** Получить пользователя по имени пользователя из JSON данных **/
    private void getUserByUsername(String jsonData,String requestId) throws Exception {
        Optional<User> user = userService.getUserByUsername(
                dtoConverter.convertToUserByUsernameDto(jsonData).getUsername());
        if (user.isPresent()) {
            sendSuccess("Пользователь найден", mapUserToUserFullDto(user.get()), requestId);
        } else {
            sendError("Пользователь не найден", requestId);
        }
    }

    /** Получить пользователя по логину из JSON данных **/
    private void getUserByLogin(String jsonData,String requestId) throws Exception {
        Optional<User> user = userService.getUserByLogin(dtoConverter.convertToUserLoginDto(jsonData));
        if (user.isPresent()) {
            sendSuccess("Пользователь найден", mapUserToUserLogin(user.get()), requestId);
        } else {
            sendError("Пользователь не найден", requestId);
        }
    }

    /** Создать нового пользователя из JSON данных **/
    private void createUser(String jsonData,String requestId) throws Exception {
        UserRegistrationDto dto = dtoConverter.convertToUserRegistrationDto(jsonData);
        User user = new User();
        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());
        user.setUsername(dto.getUsername());
        userService.saveUser(user);
        sendSuccess("Пользователь сохранен", null, requestId);
    }

    /** Обновить пользователя из JSON данных **/
    private void updateUser(String jsonData, String requestId) throws Exception {
        UserByUsernameDto dto = dtoConverter.convertToUserByUsernameDto(jsonData);
        userService.updateUser(dto);
        sendSuccess("Пользователь обновлен", null, requestId);
    }

    /** Удалить пользователя по ID из JSON данных **/
    private void deleteUser(String jsonData, String requestId) throws Exception {
        userService.deleteUser(dtoConverter.convertToIdDto(jsonData).getId());
        sendSuccess("Пользователь удален", null, requestId);
    }

    /** Получить все чаты пользователя из JSON данных **/
    private void getUserChats(String jsonData, String requestId) throws Exception {
        IdDto idDto = dtoConverter.convertToIdDto(jsonData);

        Optional<User> user = userService.getUserById(idDto.getId());
        List<Chat> chats = userService.getUserChats(idDto.getId());

        if (user.isPresent()) {
            if(chats.isEmpty()){
                sendError("Чаты не найдены", requestId);
            }else {
                sendSuccess("Чаты пользователя найдены", chats, requestId);
            }
        } else {
            sendError("Пользователь не найден", requestId);
        }
    }

    /** Получить топик для ответов **/
    @Override
    protected String getResponseTopic() {
        return "db-api_user_response";
    }

}
