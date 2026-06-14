package ru.sozvon.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sozvon.api.dto.modelsDTOs.*;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatByIdDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatCreateDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatParticipantDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatSearchDto;
import ru.sozvon.api.kafka.KafkaProducer;
import ru.sozvon.api.kafka.consumers.ChatKafkaConsumer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ChatService {
    private final KafkaProducer kafkaProducer;
    private final ChatKafkaConsumer chatKafkaConsumer;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatService(KafkaProducer kafkaProducer, ChatKafkaConsumer chatKafkaConsumer, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.chatKafkaConsumer = chatKafkaConsumer;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> getChatById(ChatByIdDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("getChatById", json);
    }

    public Map<String, Object> getUserChats(int userId) throws Exception {
        IdDto idDto = new IdDto();
        idDto.setId(userId);
        String json = objectMapper.writeValueAsString(idDto);
        return sendAndAwait("getUserChats", json);
    }

    public Map<String, Object> getAllChatMessages(ChatByIdDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("getAllChatMessages", json);
    }

    public Map<String, Object> findUserChatsByTitle(ChatSearchDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("findUserChatsByTitle", json);
    }

    public Map<String, Object> deleteChat(ChatByIdDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("deleteChat", json);
    }

    public Map<String, Object> saveChat(ChatCreateDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("saveChat", json);
    }

    public Map<String, Object> addParticipantToChat(ChatParticipantDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("addParticipantToChat", json);
    }

    public Map<String, Object> removeParticipantFromChat(ChatParticipantDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        return sendAndAwait("removeParticipantFromChat", json);
    }

    public Map<String, Object> getChatParticipants(int chatId) throws Exception {
        IdDto idDto = new IdDto();
        idDto.setId(chatId);
        String json = objectMapper.writeValueAsString(idDto);
        return sendAndAwait("getChatParticipants", json);
    }

    public boolean chatExists(int chatId) {
        try {
            ChatByIdDto dto = new ChatByIdDto();
            dto.setChatId(chatId);
            dto.setUserId(1);
            Map<String, Object> result = getChatById(dto);
            return "success".equals(result.get("status"));
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> sendAndAwait(String operation, String json) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = chatKafkaConsumer.registerRequest(requestId);

        ProducerRecord<String, String> record = new ProducerRecord<>(
                "api-db_chat_request",
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
