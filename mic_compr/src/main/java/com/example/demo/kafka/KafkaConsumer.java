package com.example.demo.kafka;

import com.example.demo.dto.PersonDTO;
import com.example.demo.models.Person;
import com.example.demo.services.PersonService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final PersonService personService;
    private final ModelMapper modelMapper;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaConsumer(PersonService personService, ModelMapper modelMapper, KafkaProducer kafkaProducer) {
        this.personService = personService;
        this.modelMapper = modelMapper;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "db_request", groupId = "db_request_group")
    public void listen(ConsumerRecord<String, PersonDTO> record, Acknowledgment ack) {
        Person person = modelMapper.map(record.value(), Person.class);
        ack.acknowledge();
        String typeOperations = record.key();

        switch (typeOperations) {
            case "getPersonByUsername":
                Person foundPerson = personService.getPersonByUsername(record.value().getUsername());
                producerRecord(foundPerson);
                break;
            case "getPersonById":
                personService.getPersonById(record.value().getId());
                break;
            case "createPerson":
                personService.createPerson(person);
                break;
            case "updatePerson":
                personService.updatePerson(person);
                break;
            case "deletePerson":
                personService.deletePerson(person);
                break;
        }
    }

    private void producerRecord(Person person) {
        PersonDTO personDTO;
        if (person == null){
            PersonDTO personDTO1 = new PersonDTO();
            personDTO1.setUsername(null);
            ProducerRecord<String, PersonDTO> producerRecord =
                    new ProducerRecord<>("db_response", "key", personDTO1);
            kafkaProducer.sendMessage(producerRecord);
        }else{
            personDTO = modelMapper.map(person,PersonDTO.class);
            ProducerRecord<String, PersonDTO> producerRecord =
                    new ProducerRecord<>("db_response", "key", personDTO);
            kafkaProducer.sendMessage(producerRecord);
        }
    }
}
