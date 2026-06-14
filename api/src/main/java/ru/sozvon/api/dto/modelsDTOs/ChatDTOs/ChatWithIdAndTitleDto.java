package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для получения чата только с id и title **/
public class ChatWithIdAndTitleDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    public ChatWithIdAndTitleDto() {}

    public ChatWithIdAndTitleDto(Integer id, String title) {
        this.id = id;
        this.title = title;
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
}
