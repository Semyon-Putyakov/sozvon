package ru.sozvon.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserRegistrationDto;
import ru.sozvon.api.security.CustomUserDetails;
import ru.sozvon.api.security.JwtUtil;
import ru.sozvon.api.services.UserService;
import ru.sozvon.api.util.ApiException;
import ru.sozvon.api.util.PasswordEncoding;
import ru.sozvon.api.validators.UserLoginValidator;
import ru.sozvon.api.validators.UserRegistrationValidator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
public class AuthController extends AbstractController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoding passwordEncoding;
    private final UserRegistrationValidator userRegistrationValidator;
    private final UserService userService;
    private final UserLoginValidator userLoginValidator;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoding passwordEncoding, UserRegistrationValidator userRegistrationValidator, UserService userService, UserLoginValidator userLoginValidator) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoding = passwordEncoding;
        this.userRegistrationValidator = userRegistrationValidator;
        this.userService = userService;
        this.userLoginValidator = userLoginValidator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto dto, BindingResult bindingResult) {
        userLoginValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(bindingResult));
        }

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            String username = userDetails.getUserDto().getUsername();
            String token = jwtUtil.generateToken(dto.getLogin());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("id", userDetails.getUserDto().getId());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException exception) {
            throw new ApiException("Неверный логин или пароль", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto, BindingResult result) throws Exception {
        userRegistrationValidator.validate(dto, result);
        if (result.hasErrors()) {
            throw new ApiException(getValidationErrorMessage(result));
        }

        String encodedPassword = passwordEncoding.encode(dto.getPassword());
        dto.setPassword(encodedPassword);

        String token = jwtUtil.generateToken(dto.getLogin());

        Map<String, Object> response = getException(userService.createUser(dto));
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleChat(ApiException e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, e.getStatus()); // e.getStatus() ВЕЗДЕ ТАК СДЕЛАТЬ
    }
}
