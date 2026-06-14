package ru.sozvon.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.*;
import ru.sozvon.api.services.MessageService;
import ru.sozvon.api.util.ApiException;
import ru.sozvon.api.validators.MessageCreateValidator;
import ru.sozvon.api.validators.MessageEditValidator;
import ru.sozvon.api.validators.MessageDeleteValidator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController extends AbstractController {
    private final MessageService messageService;
    private final MessageCreateValidator messageCreateValidator;
    private final MessageEditValidator messageEditValidator;
    private final MessageDeleteValidator messageDeleteValidator;

    @Autowired
    public MessageController(MessageService messageService, MessageCreateValidator messageCreateValidator,
                           MessageEditValidator messageEditValidator, MessageDeleteValidator messageDeleteValidator) {
        this.messageService = messageService;
        this.messageCreateValidator = messageCreateValidator;
        this.messageEditValidator = messageEditValidator;
        this.messageDeleteValidator = messageDeleteValidator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MessageCreateDto dto, BindingResult result) throws Exception {
        messageCreateValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }
        return new ResponseEntity<>(getException(messageService.createMessage(dto)), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> edit(@RequestBody MessageEditDto dto, BindingResult result) throws Exception {
        messageEditValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }
        return new ResponseEntity<>(getException(messageService.editMessage(dto)), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody MessageDeleteDto dto, BindingResult result) throws Exception {
        messageDeleteValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }
        return new ResponseEntity<>(getException(messageService.deleteMessage(dto)), HttpStatus.OK);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<?> getByChat(@PathVariable int chatId,
                                       @RequestParam int userId,
                                       @RequestParam int page,
                                       @RequestParam int size) throws Exception {
        MessagePaginationDto dto = new MessagePaginationDto();
        dto.setChatId(chatId);
        dto.setUserId(userId);
        dto.setPage(page);
        dto.setSize(size);
        return new ResponseEntity<>(getException(messageService.getMessagesByChat(dto)), HttpStatus.OK);
    }

    @GetMapping("/by-sender")
    public ResponseEntity<?> getUserMessagesBySender(@RequestParam int senderId,
                                                     @RequestParam int currentUserId,
                                                     @RequestParam int chatId) throws Exception {
        return new ResponseEntity<>(getException(messageService.getUserMessagesBySender(senderId, currentUserId, chatId)), HttpStatus.OK);
    }

    @GetMapping("/replies")
    public ResponseEntity<?> getReplies(@RequestParam int parentMessageId,
                                        @RequestParam int userId,
                                        @RequestParam int chatId) throws Exception {
        return new ResponseEntity<>(getException(messageService.getRepliesToMessage(parentMessageId, userId, chatId)), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String text,
                                    @RequestParam int userId,
                                    @RequestParam int chatId) throws Exception {
        MessageSearchDto dto = new MessageSearchDto();
        dto.setText(text);
        dto.setUserId(userId);
        dto.setChatId(chatId);
        return new ResponseEntity<>(getException(messageService.searchMessagesByText(dto)), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleChat(ApiException e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}


