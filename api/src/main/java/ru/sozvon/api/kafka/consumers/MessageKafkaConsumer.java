package ru.sozvon.api.kafka.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.sozvon.api.dto.util.DtoConverter;
import ru.sozvon.api.kafka.KafkaProducer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageKafkaConsumer extends BaseKafkaConsumer {
    private final Map<String, CompletableFuture<Map<String, Object>>> futures = new ConcurrentHashMap<>();

    @Autowired
    public MessageKafkaConsumer(KafkaProducer kafkaProducer, ObjectMapper objectMapper, DtoConverter dtoConverter) {
        super(kafkaProducer, objectMapper, dtoConverter);
    }

    @KafkaListener(topics = "db-api_message_response", groupId = "api_message_response_group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        Map<String, Object> response = getMap(record);
        String requestId = (String) response.get("requestId");

        if (requestId != null && futures.containsKey(requestId)) {
            futures.get(requestId).complete(response);
            futures.remove(requestId);
        }
        ack.acknowledge();
    }

    public CompletableFuture<Map<String, Object>> registerRequest(String requestId) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        futures.put(requestId, future);
        return future;
    }

    @Override
    protected String getResponseTopic() {
        return "";
    }
}
