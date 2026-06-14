package ru.sozvon.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.MessageDTOs.MessageEditDto;

@Component
public class MessageEditValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return MessageEditDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        MessageEditDto dto = (MessageEditDto) target;

        if (dto.getNewText() == null || dto.getNewText().trim().isEmpty()) {
            errors.rejectValue("newContent", "content.empty", "Новое содержимое сообщения не может быть пустым");
        } else {
            String content = dto.getNewText().trim();
            if (content.length() > 2000) {
                errors.rejectValue("newContent", "content.too.long", 
                    "Содержимое сообщения не может быть длиннее 2000 символов");
            }
        }
    }
}





