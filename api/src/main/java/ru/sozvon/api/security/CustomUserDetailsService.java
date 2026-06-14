package ru.sozvon.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;
import ru.sozvon.api.services.UserService;
import ru.sozvon.api.util.ApiException;

import java.util.Map;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private Optional<UserLoginDto> userLoginDto;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {

            UserLoginDto userLogin = new UserLoginDto();
            userLogin.setLogin(username);

            Map<String,Object> map = userService.getUserByLogin(userLogin);
            Object obj = map.get("object");

            UserLoginDto user = new ObjectMapper().convertValue(obj, UserLoginDto.class);
            userLoginDto = Optional.of(user);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }

        if (userLoginDto.isEmpty()) {
            throw new ApiException("Пользователь не найден");
        } else {
            return new CustomUserDetails(userLoginDto.get());
        }
    }
} 