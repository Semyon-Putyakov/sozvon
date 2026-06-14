package ru.sozvon.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;

@Component
public class UserLoginValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return UserLoginDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        UserLoginDto dto = (UserLoginDto) target;

        if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
            errors.rejectValue("login", "login.empty", "Логин не может быть пустым");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            errors.rejectValue("password", "password.empty", "Пароль не может быть пустым");
        }
    }
}
