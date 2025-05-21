package com.example.demo.services;

import com.example.demo.models.Person;
import com.example.demo.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @Transactional(readOnly = true)
    public Person getPersonById(int id) {
        Person person = personRepository.findById(id).orElse(null);
        return person;
    }

    @Transactional(readOnly = true)
    public Person getPersonByUsername(String username) {
       return personRepository.findByUsername(username).orElse(null);
    }

    public void createPerson(Person person) {
        personRepository.save(person);
    }

    public void updatePerson(Person person) {
        Person personLoaded = personRepository.findById(person.getId()).orElse(null);
        personLoaded.setUsername(person.getUsername());
        personRepository.save(personLoaded);
    }

    public void deletePerson(Person person) {
        personRepository.deleteByUsername(person.getUsername());
    }
}
