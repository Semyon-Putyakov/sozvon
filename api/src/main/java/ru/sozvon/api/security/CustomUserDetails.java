package ru.sozvon.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserLoginDto;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final UserLoginDto userLoginDto;

    public CustomUserDetails(UserLoginDto userLoginDto) {
        this.userLoginDto = userLoginDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userLoginDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userLoginDto.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserLoginDto getUserDto() {
        return userLoginDto;
    }
}
