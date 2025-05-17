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

    public String getPersonJWT(String username){
        System.out.println("before generateJWT");
        String jwtToken = generateJWT("jwtGenerate_request", username,"jwtGenerate_request");
        System.out.println("after generateJWT");
        return jwtToken;
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

    public Optional<PersonDTO> getPersonById(int id) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(id);

        String key = "getPersonById_" + id;
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
        String key = "createPerson" + personDTO.getUsername();
        producerRecord(key, personDTO,"db_request");
    }

    public String updatePersonDTO(PersonDTO personDTO) {
        String key = "updatePerson";
        producerRecord(key, personDTO, "db_request");
        producerRecordString("jwtGenerate_request", personDTO.getUsername(),"jwtGenerate_request");
        String jwtToken = generateJWT("jwtGenerate_request", personDTO.getUsername(),"jwtGenerate_request");
        return jwtToken;
    }

    public void deletePersonDTO(PersonDTO personDTO) {
        String key = "deletePerson";
        producerRecord(key, personDTO,"db_request");
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

    private String generateJWT(String key, String username, String topic){
        System.out.println("before generateJWT1");
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername(username);
        producerRecord(key, personDTO,topic);
        ConsumerRecord<String,String> consumerRecord;
        try {
            consumerRecord = kafkaConsumer.queueGenerate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String jwtToken = consumerRecord.value();
        System.out.println("after generateJWT1");
        return jwtToken;
    }

}
