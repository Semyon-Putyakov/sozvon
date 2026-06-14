package ru.sozvon.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.MessageDeleteDto;

@Component
public class MessageDeleteValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return MessageDeleteDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        MessageDeleteDto dto = (MessageDeleteDto) target;

        if (dto.getMessageId() <= 0) {
            errors.rejectValue("messageId", "message.id.invalid", "ID сообщения должен быть положительным числом");
        }
        
        if (dto.getUserId() <= 0) {
            errors.rejectValue("userId", "user.id.invalid", "ID пользователя должен быть положительным числом");
        }
    }
}





