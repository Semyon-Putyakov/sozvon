package com.example.demo.service;

import com.example.demo.dto.PersonDTO;
import com.example.demo.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonDetailsService implements UserDetailsService {

    private final PersonService personService;

    @Autowired
    public PersonDetailsService(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<PersonDTO> personDTO = personService.getPerson(username);
        if (personDTO.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        } else {
            return new PersonDetails(personDTO.get());
        }
    }
}
