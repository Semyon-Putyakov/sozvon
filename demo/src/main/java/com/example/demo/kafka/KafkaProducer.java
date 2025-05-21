package com.example.demo.kafka;

import com.example.demo.dto.PersonDTO;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, PersonDTO> kafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, PersonDTO> kafkaTemplate, KafkaTemplate<String, String> stringKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    public void sendMessage(ProducerRecord<String, PersonDTO> producerRecord) {
        kafkaTemplate.send(producerRecord);
    }

    public void sendStringMessage(ProducerRecord<String, String> producerRecord) {
        stringKafkaTemplate.send(producerRecord);
    }

}

