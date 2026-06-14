package ru.sozvon.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.MessageCreateDto;

@Component
public class MessageCreateValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return MessageCreateDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        MessageCreateDto dto = (MessageCreateDto) target;

        if (dto.getText() == null || dto.getText().trim().isEmpty()) {
            errors.rejectValue("content", "content.empty", "Содержимое сообщения не может быть пустым");
        } else {
            String content = dto.getText().trim();
            if (content.length() > 2000) {
                errors.rejectValue("content", "content.too.long", 
                    "Содержимое сообщения не может быть длиннее 2000 символов");
            }
        }
    }
}





