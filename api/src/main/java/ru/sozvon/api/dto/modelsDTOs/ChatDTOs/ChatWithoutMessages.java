package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sozvon.api.dto.modelsDTOs.UserDTOs.UserByUsernameDto;

import java.util.List;

/** DTO для полной информации о чате **/
public class ChatWithoutMessages {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("participants")
    private List<UserByUsernameDto> participants;

    public ChatWithoutMessages() {}

    public ChatWithoutMessages(Integer id, String title, List<UserByUsernameDto> participants) {
        this.id = id;
        this.title = title;
        this.participants = participants;
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

    public List<UserByUsernameDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserByUsernameDto> participants) {
        this.participants = participants;
    }
}
