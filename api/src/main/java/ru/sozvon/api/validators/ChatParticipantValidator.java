package ru.sozvon.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatParticipantDto;
import ru.sozvon.api.services.ChatService;

@Component
public class ChatParticipantValidator implements Validator {
    
    private final ChatService chatService;
    
    @Autowired
    public ChatParticipantValidator(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return ChatParticipantDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        ChatParticipantDto dto = (ChatParticipantDto) target;

        if (dto.getChatId() <= 0) {
            errors.rejectValue("chatId", "chat.id.invalid", "ID чата должен быть положительным числом");
        } else {
            if (!chatService.chatExists(dto.getChatId())) {
                errors.rejectValue("chatId", "chat.not.found", "Чат с указанным ID не существует");
            }
        }

        if (dto.getUserId() <= 0) {
            errors.rejectValue("userId", "user.id.invalid", "ID пользователя должен быть положительным числом");
        }

        if (dto.getParticipantId() <= 0) {
            errors.rejectValue("participantId", "participant.id.invalid", "ID участника должен быть положительным числом");
        }

        if (dto.getUserId() == dto.getParticipantId()) {
            errors.rejectValue("participantId", "participant.self.add", "Нельзя добавить самого себя в чат");
        }
    }
}
