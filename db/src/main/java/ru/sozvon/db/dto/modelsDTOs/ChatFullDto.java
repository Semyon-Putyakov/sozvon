package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;

import java.util.List;

/** DTO для полной информации о чате **/
public class ChatFullDto {
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("participants")
    private List<User> participants;
    
    @JsonProperty("messages")
    private List<Message> messages;

    public ChatFullDto() {}

    public ChatFullDto(Chat chat) {
        this.id = chat.getId();
        this.title = chat.getTitle();
        this.participants = chat.getParticipants();
        this.messages = chat.getMessages();
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<User> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
