package ru.sozvon.api.kafka.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import ru.sozvon.api.dto.util.DtoConverter;
import ru.sozvon.api.kafka.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

/** Базовый класс для всех Kafka consumer **/
@Component
public abstract class BaseKafkaConsumer {
    protected final KafkaProducer kafkaProducer;
    protected final ObjectMapper objectMapper;
    protected final DtoConverter dtoConverter;

    public BaseKafkaConsumer(KafkaProducer kafkaProducer, ObjectMapper objectMapper, DtoConverter dtoConverter) {
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.dtoConverter = dtoConverter;
    }

    protected Map<String, Object> getMap(ConsumerRecord<String, String> record) {
        Map<String, Object> response = new HashMap<>();
        try {
            response = dtoConverter.convertToMap(record.value());;
        } catch (JsonProcessingException e) {
            response.put("message", e);
            response.put("status", "error");
        }
        return response;
    }

    /** Получить топик для ответов **/
    protected abstract String getResponseTopic();
}
