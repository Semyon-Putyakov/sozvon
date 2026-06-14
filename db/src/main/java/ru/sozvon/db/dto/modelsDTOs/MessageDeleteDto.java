package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для удаления сообщения **/
public class MessageDeleteDto {
    
    @JsonProperty("messageId")
    private int messageId;
    
    @JsonProperty("userId")
    private int userId;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
