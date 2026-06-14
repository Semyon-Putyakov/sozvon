package ru.sozvon.api.dto.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.*;
import ru.sozvon.api.dto.modelsDTOs.IdDto;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.*;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserByUsernameDto;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserRegistrationDto;


import java.util.Map;

/** Класс для конвертации JSON в различные DTO **/
@Component
public class DtoConverter {
    
    private final ObjectMapper objectMapper;

    @Autowired
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

    /** Конвертировать JSON в UserByUsernameDto **/
    public UserByUsernameDto convertToUserByUsernameDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, UserByUsernameDto.class);
    }

    
    // Методы для работы с ChatDTO
    /** Конвертировать JSON в ChatCreateDto **/
    public ChatCreateDto convertToChatCreateDto(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, ChatCreateDto.class);
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
        return objectMapper.readValue(json, Map.class);
    }
}
