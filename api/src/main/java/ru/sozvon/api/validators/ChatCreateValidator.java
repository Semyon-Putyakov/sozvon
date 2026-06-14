package ru.sozvon.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sozvon.api.dto.modelsDTOs.ChatDTOs.ChatCreateDto;
import ru.sozvon.api.services.ChatService;

import java.util.List;

@Component
public class ChatCreateValidator implements Validator {
    
    private final ChatService chatService;
    
    @Autowired
    public ChatCreateValidator(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return ChatCreateDto.class.equals(clazz);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        ChatCreateDto dto = (ChatCreateDto) target;

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.rejectValue("title", "title.empty", "Название чата не может быть пустым");
        } else if (dto.getTitle().trim().length() < 2) {
            errors.rejectValue("title", "title.too.short", "Название чата должно содержать минимум 2 символа");
        } else if (dto.getTitle().trim().length() > 100) {
            errors.rejectValue("title", "title.too.long", "Название чата не может быть длиннее 100 символов");
        }

        List<Integer> participantIds = dto.getParticipantIds();
        if (participantIds == null || participantIds.isEmpty()) {
            errors.rejectValue("participantIds", "participants.empty", "Чат должен содержать минимум одного участника");
        } else if (participantIds.size() > 100) {
            errors.rejectValue("participantIds", "participants.too.many", "Чат не может содержать более 100 участников");
        } else {
            long uniqueCount = participantIds.stream().distinct().count();
            if (uniqueCount != participantIds.size()) {
                errors.rejectValue("participantIds", "participants.duplicates", "Список участников содержит дублирующиеся ID");
            }

            for (int i = 0; i < participantIds.size(); i++) {
                Integer participantId = participantIds.get(i);
                if (participantId == null || participantId <= 0) {
                    errors.rejectValue("participantIds[" + i + "]", "participant.invalid.id", 
                        "ID участника должен быть положительным числом");
                }
            }
        }

        if (dto.getIsGroup() != null && participantIds != null) {
            if (dto.getIsGroup() && participantIds.size() < 2) {
                errors.rejectValue("isGroup", "group.chat.min.participants", 
                    "Групповой чат должен содержать минимум 2 участника");
            } else if (!dto.getIsGroup() && participantIds.size() != 2) {
                errors.rejectValue("isGroup", "private.chat.participants", 
                    "Приватный чат должен содержать ровно 2 участника");
            }
        }
    }
}
