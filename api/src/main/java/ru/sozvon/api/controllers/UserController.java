package ru.sozvon.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserByUsernameDto;
import ru.sozvon.api.services.UserService;
import ru.sozvon.api.util.ApiException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController extends AbstractController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) throws Exception {
        return new ResponseEntity<>(getException(userService.getUserById(id)), HttpStatus.OK);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) throws Exception {
        return new ResponseEntity<>(getException(userService.getUserByUsername(username)), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserByUsernameDto dto) throws Exception {
        return new ResponseEntity<>(getException(userService.updateUser(dto)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(getException(userService.deleteUser(id)), HttpStatus.OK);
    }

    @GetMapping("/{id}/chats")
    public ResponseEntity<?> getUserChats(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(getException(userService.getUserChats(id)), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleChat(ApiException e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}


