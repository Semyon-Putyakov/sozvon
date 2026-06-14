package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;

import java.util.List;

/** DTO для полного пользователя с чатами и сообщениями **/
public class UserFullDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("username")
    private String username;
    
    @JsonProperty("chats")
    private List<Chat> chats;

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

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

}
