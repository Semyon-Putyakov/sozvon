package com.example.demo.controller;


import com.example.demo.dto.PersonDTO;
import com.example.demo.security.PersonDetails;
import com.example.demo.service.PersonService;
import com.example.demo.validation.PersonModel;
import com.example.demo.validation.PersonValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@org.springframework.stereotype.Controller
@RequestMapping("/auth")
public class Controller {
    private final PersonService personService;
    private final PersonValidator personValidator;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Autowired
    public Controller(PersonService personService, PersonValidator personValidator, AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person")PersonModel personModel) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String putPerson(@ModelAttribute("person") @Valid PersonModel personModel, BindingResult bindingResult) {
        PersonDTO personDTO = modelMapper.map(personModel, PersonDTO.class);

        personValidator.validate(personModel, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }
        personService.createPersonDTO(personDTO);
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("person", new PersonDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String performLogin(@ModelAttribute("person") PersonModel personModel, HttpServletResponse response, Model model) {
        PersonDTO personDTO = modelMapper.map(personModel, PersonDTO.class);
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(personDTO.getUsername(), personDTO.getPassword());
            authenticationManager.authenticate(authToken);
            String jwt = personService.getPersonJWT(personDTO);
            setCookie(response, jwt);
            return "redirect:/auth/user";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Неверные учетные данные");
            return "auth/login";
        }
    }

    @GetMapping("/user")
    public String user(Model model) {
        PersonDTO personDTO = getUserDetails().getPerson();
        System.out.println(personDTO.getUsername());
        model.addAttribute("person", personDTO);
        return "afterPage/personPage";
    }

    @DeleteMapping("/delete")
    public String delete(HttpServletResponse response) {
        PersonDTO person = new PersonDTO();
        person.setUsername(getUserDetails().getUsername());
        personService.deletePersonDTO(person);

        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        System.out.println("Security cookie 1: " + SecurityContextHolder.getContext());
        SecurityContextHolder.clearContext();
        System.out.println("Security cookie 2: " + SecurityContextHolder.getContext());
        return "other/refreshLogin";
    }

    @GetMapping("/update")
    public String update(Model model) {
        String username = getUserDetails().getUsername();
        PersonDTO personDTO = personService.getPerson(username).orElse(null);
        model.addAttribute("person", personDTO);
        return "afterPage/update";
    }

    @PatchMapping("/update")
    public String update(@ModelAttribute("person") @Valid PersonModel personModel, BindingResult bindingResult, HttpServletResponse response) {
        personValidator.validate(personModel, bindingResult);
        if (bindingResult.hasErrors()) {
            return "afterPage/update";
        }
        PersonDTO personDTO = modelMapper.map(personModel, PersonDTO.class);
        String jwt = personService.updatePersonDTO(personDTO);
        System.out.println("Security cookie 1: " + SecurityContextHolder.getContext());

        SecurityContextHolder.clearContext();
        deleteCookie(response);

        setCookie(response, jwt);

        System.out.println("Security cookie 2: " + SecurityContextHolder.getContext());
        return "other/refresh";
    }

    private PersonDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails userDetails = (PersonDetails) authentication.getPrincipal();
        return userDetails;
    }

    private void setCookie(HttpServletResponse response, String jwt) {
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void deleteCookie(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }
}
