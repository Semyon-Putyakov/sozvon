package ru.sozvon.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatByIdDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatCreateDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatParticipantDto;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatSearchDto;
import ru.sozvon.api.services.ChatService;
import ru.sozvon.api.util.ApiException;
import ru.sozvon.api.validators.ChatCreateValidator;
import ru.sozvon.api.validators.ChatParticipantValidator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController extends AbstractController {
    private final ChatService chatService;
    private final ChatCreateValidator chatCreateValidator;
    private final ChatParticipantValidator chatParticipantValidator;

    @Autowired
    public ChatController(ChatService chatService, ChatCreateValidator chatCreateValidator, 
                         ChatParticipantValidator chatParticipantValidator) {
        this.chatService = chatService;
        this.chatCreateValidator = chatCreateValidator;
        this.chatParticipantValidator = chatParticipantValidator;
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatById(@PathVariable int chatId, @RequestParam int userId) throws Exception {
        ChatByIdDto dto = new ChatByIdDto();
        dto.setChatId(chatId);
        dto.setUserId(userId);
        return new ResponseEntity<>(getException(chatService.getChatById(dto)), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserChats(@PathVariable int userId) throws Exception {
        return new ResponseEntity<>(getException(chatService.getUserChats(userId)), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findUserChatsByTitle(@RequestParam String title, @RequestParam int userId) throws Exception {
        ChatSearchDto dto = new ChatSearchDto();
        dto.setTitle(title);
        dto.setUserId(userId);
        return new ResponseEntity<>(getException(chatService.findUserChatsByTitle(dto)), HttpStatus.OK);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable int chatId, @RequestParam int userId) throws Exception {
        ChatByIdDto dto = new ChatByIdDto();
        dto.setChatId(chatId);
        dto.setUserId(userId);
        return new ResponseEntity<>(getException(chatService.deleteChat(dto)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody ChatCreateDto dto, BindingResult result) throws Exception {
        chatCreateValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }
        return new ResponseEntity<>(getException(chatService.saveChat(dto)), HttpStatus.OK);
    }

    /** Добавить участника в чат **/
    @PostMapping("/{chatId}/participants")
    public ResponseEntity<?> addParticipantToChat(@PathVariable int chatId, @RequestBody ChatParticipantDto dto, BindingResult result) throws Exception {
        dto.setChatId(chatId);
        chatParticipantValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }
        return new ResponseEntity<>(getException(chatService.addParticipantToChat(dto)), HttpStatus.OK);
    }

    /** Удалить участника из чата **/
    @DeleteMapping("/{chatId}/participants/{participantId}")
    public ResponseEntity<?> removeParticipantFromChat(@PathVariable int chatId, @PathVariable int participantId, @RequestParam int userId) throws Exception {
        ChatParticipantDto dto = new ChatParticipantDto(chatId, userId, participantId);
        return new ResponseEntity<>(getException(chatService.removeParticipantFromChat(dto)), HttpStatus.OK);
    }

    /** Получить список участников чата **/
    @GetMapping("/{chatId}/participants")
    public ResponseEntity<?> getChatParticipants(@PathVariable int chatId) throws Exception {
        return new ResponseEntity<>(getException(chatService.getChatParticipants(chatId)), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleChat(ApiException e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}


