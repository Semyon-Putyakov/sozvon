package ru.sozvon.api.dto.modelsDTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для операции регистрации пользователя **/
public class UserRegistrationDto {
    @JsonProperty("login")
    private String login;
    
    @JsonProperty("password")
   private String password;
    
    @JsonProperty("username")
    private String username;

    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

}
