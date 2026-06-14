package ru.sozvon.api.controllers;

import org.springframework.validation.BindingResult;
import ru.sozvon.api.util.ApiException;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController {
    protected Map<String, Object> getException(Map<String, Object> result) {
        String status = (String) result.get("status");
        String message = (String) result.get("message");
        Object object = result.get("object");

        if (status.equals("error")) {
            throw new ApiException(message);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("object", object);
        response.put("message", message);

        return response;
    }

    // не получается красиво взять ошибки из BindingResult login - Логин не может быть пустым; password - Пароль не может быть пустым;
    // англ убрать хотя не норм но наддо сделать лучше в UserLoginValidator
    protected String getValidationErrorMessage(BindingResult result) {
        StringBuilder sb = new StringBuilder("Ошибки валидации: ");
        result.getFieldErrors().forEach(error ->
                sb.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );
        return sb.toString();
    }
}
