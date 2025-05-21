package com.example.demo.validation;


import com.example.demo.dto.PersonDTO;
import com.example.demo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;

    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonModel validatedPerson = (PersonModel) target;
        var personResponse = personService.getPerson(validatedPerson.getUsername()).orElse(null);
        if (personResponse.getUsername() != null) {
            errors.rejectValue("username", null, "Такой пользователь уже существует");
        }
    }
}
