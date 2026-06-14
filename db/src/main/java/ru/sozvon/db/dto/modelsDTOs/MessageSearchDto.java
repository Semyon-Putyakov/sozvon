package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для поиска сообщений **/
public class MessageSearchDto {
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("userId")
    private int userId;

    @JsonProperty("chatId")
    private int chatId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
