package ru.sozvon.db.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.sozvon.db.dto.modelsDTOs.UserByUsernameDto;
import ru.sozvon.db.dto.modelsDTOs.UserFullDto;
import ru.sozvon.db.dto.modelsDTOs.UserLoginDto;
import ru.sozvon.db.kafka.KafkaProducer;
import ru.sozvon.db.models.User;

import java.util.HashMap;
import java.util.Map;
/** Базовый класс для всех Kafka consumer **/
@Component
public abstract class BaseKafkaConsumer {
    protected final KafkaProducer kafkaProducer;
    protected final ObjectMapper objectMapper;

    public BaseKafkaConsumer(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    /** Отправляет успешный ответ **/
    protected void sendSuccess(String message, Object data, String requestId){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("object", data);
        response.put("requestId", requestId);

        String json;
        try {
            json = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            json = "{\"status\":\"" + "error" + "\",\"message\":\"" + e + "\",\"requestId\":\"" + requestId + "\"}";
        }
        sendMessage(getResponseTopic(), json);
    }

    /** Отправляет ответ с ошибкой **/
    protected void sendError(String errorMessage, String requestId){
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", errorMessage);
        response.put("requestId", requestId);

        String json;
        try {
            json = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            json = "{\"status\":\"" + "error" + "\",\"message\":\"" + e + "\",\"requestId\":\"" + requestId + "\"}";
        }
        sendMessage(getResponseTopic(), json);
    }

    protected String[] splitOperationAndRequestId(String key) {
        int underscoreIndex = key.indexOf('_');
        if (underscoreIndex == -1) {
            return new String[]{key, null};
        }
        String operation = key.substring(0, underscoreIndex);
        String requestId = key.substring(underscoreIndex + 1);
        return new String[]{operation, requestId};
    }

    private void sendMessage(String responseTopic, String json){
        ProducerRecord<String, String> record = new ProducerRecord<>(responseTopic,"key" ,json);
        kafkaProducer.sendMessage(record);
    }

    /** Маппинг User в UserFullDto **/
    protected UserFullDto mapUserToUserFullDto(User user) {
        UserFullDto dto = new UserFullDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    protected UserLoginDto mapUserToUserLogin(User user) {
        UserLoginDto dto = new UserLoginDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setPassword(user.getPassword());
        dto.setUsername(user.getUsername());
        return dto;
    }

    protected UserByUsernameDto mapUserToUserByUsernameDto(User user) {
        UserByUsernameDto dto = new UserByUsernameDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    /** Получить топик для ответов **/
    protected abstract String getResponseTopic();
}