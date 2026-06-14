package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для операций по ID **/
public class IdDto {
    @JsonProperty("id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
