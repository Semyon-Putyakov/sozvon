package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;


/** DTO для редактирования сообщения **/
public class MessageEditDto {
    
    @JsonProperty("messageId")
    private int messageId;
    
    @JsonProperty("newText")
    private String newText;

    @JsonProperty("senderId")
    private int senderId;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
}
