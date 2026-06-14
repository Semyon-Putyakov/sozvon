package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO для пагинации сообщений **/
public class MessagePaginationDto {
    
    @JsonProperty("chatId")
    private int chatId;
    
    @JsonProperty("userId")
    private int userId;
    
    @JsonProperty("page")
    private int page;
    
    @JsonProperty("size")
    private int size;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
