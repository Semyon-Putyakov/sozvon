package ru.sozvon.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.*;
import ru.sozvon.api.kafka.KafkaProducer;
import ru.sozvon.api.kafka.consumers.MessageKafkaConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class MessageService {
    private final KafkaProducer kafkaProducer;
    private final MessageKafkaConsumer messageKafkaConsumer;
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageService(KafkaProducer kafkaProducer, MessageKafkaConsumer messageKafkaConsumer, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.messageKafkaConsumer = messageKafkaConsumer;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> createMessage(MessageCreateDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("createMessage", json);
    }

    public Map<String, Object> editMessage(MessageEditDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("editMessage", json);
    }

    public Map<String, Object> deleteMessage(MessageDeleteDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("deleteMessage", json);
    }

    public Map<String, Object> getMessagesByChat(MessagePaginationDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("getMessagesByChat", json);
    }

    public Map<String, Object> getUserMessagesBySender(int senderId, int currentUserId, int chatId) throws Exception {
        Map<String,Object> replies = new HashMap<>();
        replies.put("senderId", senderId);
        replies.put("currentUserId", currentUserId);
        replies.put("chatId", chatId);


        String json = objectMapper.writeValueAsString(replies);
        return sendAndAwait("getUserMessagesBySender", json);
    }

    public Map<String, Object> getRepliesToMessage(int parentMessageId, int userId, int chatId) throws Exception {
        Map<String,Object> replies = new HashMap<>();
        replies.put("parentMessageId", parentMessageId);
        replies.put("userId", userId);
        replies.put("chatId", chatId);

        String json = objectMapper.writeValueAsString(replies);
        return sendAndAwait("getRepliesToMessage", json);
    }

    public Map<String, Object> searchMessagesByText(MessageSearchDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("searchMessagesByText", json);
    }

    private Map<String, Object> sendAndAwait(String operation, String json) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = messageKafkaConsumer.registerRequest(requestId);

        ProducerRecord<String, String> record = new ProducerRecord<>(
                "api-db_message_request",
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
