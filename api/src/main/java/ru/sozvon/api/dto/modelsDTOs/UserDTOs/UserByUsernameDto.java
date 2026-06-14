package ru.sozvon.api.dto.modelsDTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для получения пользователя по username **/
public class UserByUsernameDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("username")
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
