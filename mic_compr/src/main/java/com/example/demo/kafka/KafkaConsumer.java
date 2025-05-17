package com.example.demo.kafka;


import com.example.demo.dto.PersonDTO;
import com.example.demo.models.Person;
import com.example.demo.services.PersonService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final PersonService personService;
    private final ModelMapper modelMapper;

    @Autowired
    public KafkaConsumer(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @KafkaListener(topics = "db_request", groupId = "db_request_group")
    public void listen(ConsumerRecord<String, PersonDTO> record, Acknowledgment ack) {
        Person person = modelMapper.map(record.value(), Person.class);
        System.out.println(person.getUsername());
        System.out.println(person.getPassword());

        ack.acknowledge();

        String key = record.key();
        if (key == null) {
            return;
        }

        String[] parts = key.split("_");
        if (parts.length == 0) {
            return;
        }

        String typeOperations = parts[0];

        switch (typeOperations) {
            case "getPersonByUsername":
                personService.getPersonByName(record.value().getUsername());
                break;
            case "getPersonById":
                personService.getPersonById(record.value().getId());
            case "createPerson":
                personService.createPerson(person);
                System.out.println(1);
                break;
            case "updatePerson":
                personService.updatePerson(person);
                break;
            case "deletePerson":
                personService.deletePerson(person);
                break;
        }
    }
}
