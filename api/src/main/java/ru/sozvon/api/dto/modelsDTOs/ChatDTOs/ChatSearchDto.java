package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для поиска чатов по названия **/
public class ChatSearchDto {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("userId")
    private int userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
