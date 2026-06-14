package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для получения чата по ID с проверкой пользователя **/
public class ChatByIdDto {
    @JsonProperty("chatId")
    private int chatId;
    
    @JsonProperty("userId")
    private int userId;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
