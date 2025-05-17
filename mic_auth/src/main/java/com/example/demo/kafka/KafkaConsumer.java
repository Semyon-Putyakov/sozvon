package com.example.demo.kafka;

import com.example.demo.dto.PersonDTO;
import com.example.demo.util.jwtUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final jwtUtil jwtUtil;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaConsumer(com.example.demo.util.jwtUtil jwtUtil, KafkaProducer kafkaProducer) {
        this.jwtUtil = jwtUtil;
        this.kafkaProducer = kafkaProducer;
    }


    @KafkaListener(
            topics = "jwtGenerate_request",
            groupId = "jwt_request_generate",
            containerFactory = "GenerateListenerFactory"
    )
    public void listenGenerate(ConsumerRecord<String, PersonDTO> record, Acknowledgment ack) {
        String token = jwtUtil.generateToken(record.value().getUsername());

        ProducerRecord<String, String> producerRecord
                = new ProducerRecord<>("jwtGenerate_response", record.key(), token);

        ack.acknowledge();

        kafkaProducer.sendMessage(producerRecord);
    }


    @KafkaListener(
            topics = "jwtValidate_request",
            groupId = "jwt_request_validate",
            containerFactory = "ValidateListenerFactory"
    )
    public void listenValidate(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String username = jwtUtil.validateTokenAndRetrieveClaim(record.value());

        ProducerRecord<String, String> producerRecord
                = new ProducerRecord<>("jwtValidate_response", record.key(), username);

        ack.acknowledge();

        kafkaProducer.sendMessage(producerRecord);
    }
}
