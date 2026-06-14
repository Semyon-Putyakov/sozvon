package ru.sozvon.db.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.sozvon.db.dto.modelsDTOs.*;
import ru.sozvon.db.models.Chat;

import java.util.Map;

/** Класс для конвертации JSON в различные DTO **/
@Component
public class DtoConverter {
    
    private final ObjectMapper objectMapper;
    
    public DtoConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    // Методы для работы с UserDTO
    /** Конвертировать JSON в UserLoginDto **/
    public UserLoginDto convertToUserLoginDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserLoginDto.class);
    }
    
    /** Конвертировать JSON в UserRegistrationDto **/
    public UserRegistrationDto convertToUserRegistrationDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserRegistrationDto.class);
    }
    
    /** Конвертировать JSON в UserFullDto **/
    public UserFullDto convertToUserFullDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserFullDto.class);
    }

    /** Конвертировать JSON в UserByUsernameDto **/
    public UserByUsernameDto convertToUserByUsernameDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserByUsernameDto.class);
    }

    
    // Методы для работы с ChatDTO
    /** Конвертировать JSON в ChatCreateDto **/
    public ChatCreateDto convertToChatCreateDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatCreateDto.class);
    }
    
    /** Конвертировать JSON в ChatFullDto **/
    public ChatFullDto convertToChatFullDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatFullDto.class);
    }
    
    /** Конвертировать Chat в ChatFullDto **/
    public ChatFullDto convertChatToChatFullDto(Chat chat) {
        ChatFullDto chatFullDto = new ChatFullDto();
        chatFullDto.setId(chat.getId());
        chatFullDto.setMessages(chat.getMessages());
        chatFullDto.setParticipants(chat.getParticipants());
        chatFullDto.setTitle(chat.getTitle());
        return chatFullDto;
    }
    
    /** Конвертировать JSON в ChatByIdDto **/
    public ChatByIdDto convertToChatByIdDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatByIdDto.class);
    }
    
    /** Конвертировать JSON в ChatSearchDto **/
    public ChatSearchDto convertToChatSearchDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatSearchDto.class);
    }

    /** Конвертировать JSON в ChatWithoutMessages **/
    public ChatWithoutMessages convertToChatWithoutMessages(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatWithoutMessages.class);
    }

    /** Конвертировать JSON в ChatWithIdAndTitleDto **/
    public ChatWithIdAndTitleDto convertToChatWithIdAndTitleDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatWithIdAndTitleDto.class);
    }

    /** Конвертировать JSON в ChatParticipantDto **/
    public ChatParticipantDto convertToChatParticipantDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatParticipantDto.class);
    }

    
    // Методы для работы с MessageDTO
    /** Конвертировать JSON в MessageCreateDto **/
    public MessageCreateDto convertToMessageCreateDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessageCreateDto.class);
    }
    
    /** Конвертировать JSON в MessageFullDto **/
    public MessageFullDto convertToMessageFullDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessageFullDto.class);
    }
    
    /** Конвертировать JSON в MessageEditDto **/
    public MessageEditDto convertToMessageEditDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessageEditDto.class);
    }
    
    /** Конвертировать JSON в MessageDeleteDto **/
    public MessageDeleteDto convertToMessageDeleteDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessageDeleteDto.class);
    }
    
    /** Конвертировать JSON в MessageSearchDto **/
    public MessageSearchDto convertToMessageSearchDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessageSearchDto.class);
    }
    
    /** Конвертировать JSON в MessagePaginationDto **/
    public MessagePaginationDto convertToMessagePaginationDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MessagePaginationDto.class);
    }



    
    // Общие методы для работы
    /** Конвертировать JSON в IdDto **/
    public IdDto convertToIdDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, IdDto.class);
    }
    
    /** Конвертировать JSON в Map **/
    public Map<String, Object> convertToMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, java.util.Map.class);
    }
}
