package ru.sozvon.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sozvon.api.dto.modelsDTOs.*;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserByUsernameDto;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserRegistrationDto;
import ru.sozvon.api.kafka.KafkaProducer;
import ru.sozvon.api.kafka.consumers.UserKafkaConsumer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final KafkaProducer kafkaProducer;
    private final UserKafkaConsumer userKafkaConsumer;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(KafkaProducer kafkaProducer, UserKafkaConsumer userKafkaConsumer, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.userKafkaConsumer = userKafkaConsumer;
        this.objectMapper = objectMapper;
    }

    /** Получить пользователя по id пользователя **/
    public Map<String, Object> getUserById(int id) throws Exception {
        IdDto idDto = new IdDto();
        idDto.setId(id);

        String json = objectMapper.writeValueAsString(idDto);
        return sendAndAwait("getUserById", json);
    }

    /** Получить пользователя по имени пользователя **/
    public Map<String, Object> getUserByUsername(String username) throws Exception {
        UserByUsernameDto dto = new UserByUsernameDto();
        dto.setUsername(username);
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("getUserByUsername", json);
    }

    /** Получить пользователя по логину **/
    public Map<String, Object> getUserByLogin(UserLoginDto login) throws Exception {

        String json = objectMapper.writeValueAsString(login);
        return sendAndAwait("getUserByLogin", json);
    }

    /** Создать нового пользователя **/
    public Map<String, Object> createUser(UserRegistrationDto userRegistrationDto) throws Exception {
        String json = objectMapper.writeValueAsString(userRegistrationDto);
        return sendAndAwait("createUser", json);
    }

    /** Обновить пользователя **/
    public Map<String, Object> updateUser(UserByUsernameDto userByUsernameDto) throws Exception {
        String json = objectMapper.writeValueAsString(userByUsernameDto);
        return sendAndAwait("updateUser", json);
    }

    /** Удалить пользователя **/
    public Map<String, Object> deleteUser(Integer id) throws Exception {
        IdDto idDto = new IdDto();
        idDto.setId(id);
        String json = objectMapper.writeValueAsString(idDto);
        return sendAndAwait("deleteUser", json);
    }

    /** Получить все чаты пользователя **/
    public Map<String, Object> getUserChats(Integer id) throws Exception {
        IdDto idDto = new IdDto();
        idDto.setId(id);
        String json = objectMapper.writeValueAsString(idDto);
        return sendAndAwait("getUserChats", json);
    }

    /** Проверить существование пользователя по логину **/
    public boolean isLoginExists(String login) {
        try {
            UserLoginDto loginDto = new UserLoginDto();
            loginDto.setLogin(login);
            loginDto.setPassword(""); // Пустой пароль для проверки существования
            Map<String, Object> result = getUserByLogin(loginDto);
            return "success".equals(result.get("status"));
        } catch (Exception e) {
            return false;
        }
    }

    /** Проверить существование пользователя по имени пользователя **/
    public boolean isUsernameExists(String username) {
        try {
            Map<String, Object> result = getUserByUsername(username);
            return "success".equals(result.get("status"));
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> sendAndAwait(String operation, String json) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = userKafkaConsumer.registerRequest(requestId);

        ProducerRecord<String, String> record = new ProducerRecord<>(
                "api-db_user_request",
                operation + "_" + requestId,
                json
        );
        kafkaProducer.sendMessage(record);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return Map.of("message", "Время вышло");
        } catch (Exception e) {
            return Map.of("message", "Ошибка: " + e.getMessage());
        }
    }

}
