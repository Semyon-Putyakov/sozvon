package ru.sozvon.db.dto.modelsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sozvon.db.models.Message;
import java.util.List;

public class MessageRepliesDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("text")
    private String text;

    @JsonProperty("replies")
    private List<Message> replies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }

    // Дополнительные конструкторы для проекций в репозитории
    public MessageRepliesDto(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public MessageRepliesDto(int id, String text, List<Message> replies) {
        this.id = id;
        this.text = text;
        this.replies = replies;
    }
}
