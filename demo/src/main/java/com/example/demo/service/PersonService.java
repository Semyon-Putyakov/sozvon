package com.example.demo.service;

import com.example.demo.dto.PersonDTO;
import com.example.demo.kafka.KafkaConsumer;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.util.PasswordEncoding;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {
    private final KafkaConsumer kafkaConsumer;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public PersonService(KafkaConsumer kafkaConsumer, KafkaProducer kafkaProducer) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
    }

    public String getPersonJWT(PersonDTO personDTO){
        return generateJWT("jwtGenerate_request",personDTO,"jwtGenerate_request");
    }

    public Optional<PersonDTO> getPerson(String username){
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername(username);

        String key = "getPersonByUsername";
        producerRecord(key, personDTO,"db_request");

        PersonDTO personDTODB;
        try {
            personDTODB = kafkaConsumer.getQueue().value();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(personDTODB);
    }

    public void createPersonDTO(PersonDTO personDTO) {
        personDTO.setPassword(PasswordEncoding.encode(personDTO.getPassword()));
        String key = "createPerson";
        producerRecord(key, personDTO,"db_request");
    }

    public String updatePersonDTO(PersonDTO personDTO) {
        String key = "updatePerson";
        producerRecord(key, personDTO, "db_request");
        return generateJWT("jwtGenerate_request", personDTO,"jwtGenerate_request");
    }

    public void deletePersonDTO(PersonDTO personDTO) {
        System.out.println("before deletePersonDTO");
        String key = "deletePerson";
        producerRecord(key, personDTO,"db_request");
        System.out.println("after");
    }

    private void producerRecord(String key, PersonDTO personDTO,String topic) {
        ProducerRecord<String, PersonDTO> producerRecord =
                new ProducerRecord<>(topic, key, personDTO);
        kafkaProducer.sendMessage(producerRecord);
    }

    private void producerRecordString(String key, String string ,String topic) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(topic, key, string);
        kafkaProducer.sendStringMessage(producerRecord);
    }

    private String generateJWT(String key, PersonDTO personDTO, String topic){
        producerRecord(key,personDTO,topic);
        ConsumerRecord<String,String> consumerRecord;
        try {
            consumerRecord = kafkaConsumer.queueGenerate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return consumerRecord.value();
    }

}
