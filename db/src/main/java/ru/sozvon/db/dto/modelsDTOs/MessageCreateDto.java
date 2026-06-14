package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для создания сообщения **/
public class MessageCreateDto {
    @JsonProperty("chatId")
    private int chatId;
    
    @JsonProperty("senderId")
    private int senderId;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("parentMessageId")
    private Integer parentMessageId;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Integer parentMessageId) {
        this.parentMessageId = parentMessageId;
    }
}
