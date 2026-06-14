package ru.sozvon.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserRegistrationDto;
import ru.sozvon.api.services.UserService;

import java.util.regex.Pattern;

@Component
public class UserRegistrationValidator implements Validator {
    
    private final UserService userService;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^.{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    //        "^" +            начало строки
    //        "(?=.*[a-z])" +  минимум 1 строчная буква
    //        "(?=.*[A-Z])" +  минимум 1 заглавная буква
    //        "(?=.*\\d)" +    минимум 1 цифра
    //        ".{8,}" +        любые символы, минимум 8
    //        "$"              конец строки
    
    @Autowired
    public UserRegistrationValidator(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationDto dto = (UserRegistrationDto) target;

        validateLogin(dto.getLogin(), errors);
        validatePassword(dto.getPassword(), errors);
        validateUsername(dto.getUsername(), errors);
    }
    
    private void validateLogin(String login, Errors errors) {
        if (login == null || login.trim().isEmpty()) {
            errors.rejectValue("login", "login.empty", "Логин не может быть пустым");
            return;
        }
        
        login = login.trim();
        
        if (!USERNAME_PATTERN.matcher(login).matches()) {
            errors.rejectValue("login", "login.invalid.format", 
                "Длина логина должна быть от 3 до 20 символов");
            return;
        }

        if (userService.isLoginExists(login)) {
            errors.rejectValue("login", "login.already.exists", "Пользователь с таким логином уже существует");
        }
    }
    
    private void validateUsername(String username, Errors errors) {
        if (username == null || username.trim().isEmpty()) {
            errors.rejectValue("username", "username.empty", "Имя пользователя не может быть пустым");
            return;
        }
        
        username = username.trim();
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.rejectValue("username", "username.invalid.format", 
                "Имя пользователя должно содержать только буквы, цифры и подчеркивания, длина от 3 до 20 символов");
            return;
        }

        if (userService.isUsernameExists(username)) {
            errors.rejectValue("username", "username.already.exists", "Пользователь с таким именем уже существует");
        }
    }

    private void validatePassword(String password, Errors errors) {
        if (password == null || password.isEmpty()) {
            errors.rejectValue("password", "password.empty", "Пароль не может быть пустым");
            return;
        }
        
        if (password.length() < 8) {
            errors.rejectValue("password", "password.too.short", "Пароль должен содержать минимум 8 символов");
            return;
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errors.rejectValue("password", "password.weak", 
                "Пароль должен содержать минимум одну заглавную букву, одну строчную букву и одну цифру");
        }
    }
}
