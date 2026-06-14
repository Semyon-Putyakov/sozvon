package ru.sozvon.api.dto.modelsDTOs.ChatDTOs;

/** DTO для получения пользователей чата **/
public class ChatParticipantDto {
    private int chatId;
    private int userId;
    private int participantId;

    public ChatParticipantDto() {
    }

    public ChatParticipantDto(int chatId, int userId, int participantId) {
        this.chatId = chatId;
        this.userId = userId;
        this.participantId = participantId;
    }

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

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }
}
