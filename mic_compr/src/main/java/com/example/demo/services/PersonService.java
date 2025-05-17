package com.example.demo.services;

import com.example.demo.dto.PersonDTO;
import com.example.demo.kafka.KafkaProducer;
import com.example.demo.models.Person;
import com.example.demo.repositories.PersonRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final KafkaProducer kafkaProducer;
    private final ModelMapper modelMapper;


    @Autowired
    public PersonService(PersonRepository personRepository, KafkaProducer kafkaProducer, ModelMapper modelMapper) {
        this.personRepository = personRepository;
        this.kafkaProducer = kafkaProducer;
        this.modelMapper = modelMapper;
    }


    @Transactional(readOnly = true)
    public Person getPersonById(int id) {
        Person person = personRepository.findById(id).orElse(null);
        return person;
    }

    @Transactional(readOnly = true)
    public void getPersonByName(String name) {

        Person person = personRepository.findByUsername(name).orElse(null);

        producerRecord("getPersonById_ " + name, person);
    }

    public void createPerson(Person person) {
        personRepository.save(person);
        System.out.println(123);
    }

    public void updatePerson(Person person) {
        Person personLoaded = personRepository.findByUsername(person.getUsername()).orElse(null);
        personLoaded.setUsername(person.getUsername());
        personRepository.save(personLoaded);
    }

    public void deletePerson(Person person) {
        personRepository.delete(person);
    }

    private void producerRecord(String key, Person person) {
        PersonDTO personDTO = modelMapper.map(person,PersonDTO.class);
        ProducerRecord<String, PersonDTO> producerRecord =
                new ProducerRecord<>("db_response", key, personDTO);
        kafkaProducer.sendMessage(producerRecord);
    }

}
