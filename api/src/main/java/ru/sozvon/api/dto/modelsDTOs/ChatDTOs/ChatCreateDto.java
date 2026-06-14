package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** DTO для создания чата **/
public class ChatCreateDto {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("isGroup")
    private Boolean isGroup;
    
    @JsonProperty("participantIds")
    private List<Integer> participantIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Integer> participantIds) {
        this.participantIds = participantIds;
    }
}
