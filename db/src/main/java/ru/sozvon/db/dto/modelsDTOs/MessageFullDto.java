package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;

import java.time.LocalDateTime;
import java.util.List;

/** DTO для полного сообщения с отправителем, чатом и ответами **/
public class MessageFullDto {
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("chat")
    private Chat chat;
    
    @JsonProperty("sender")
    private User sender;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("sentAt")
    private LocalDateTime sentAt;
    
    @JsonProperty("editedAt")
    private LocalDateTime editedAt;
    
    @JsonProperty("parentMessage")
    private Message parentMessage;
    
    @JsonProperty("replies")
    private List<Message> replies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }
}
