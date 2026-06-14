package ru.sozvon.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sozvon.db.dto.modelsDTOs.*;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;
import ru.sozvon.db.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /** Создать новое сообщение только если пользователь является участником чата **/
    public void createMessage(Optional<User> userOpt, Optional<ChatWithoutMessages> chatOpt, MessageCreateDto dto) {
        Chat chat = new Chat();
        chat.setId(chatOpt.get().getId());

        Message message = new Message();
        message.setChat(chat);
        message.setSender(userOpt.get());
        message.setText(dto.getText());
        message.setSentAt(LocalDateTime.now());

        if(dto.getParentMessageId() != null){
            Message parentMessage = new Message();
            parentMessage.setId(dto.getParentMessageId());
            message.setParentMessage(parentMessage);
        }
        messageRepository.save(message);

    }

    /** Изменить сообщение только если пользователь является отправителем **/
    public void editMessage(MessageEditDto message, User user) {
        Optional<Message> messageOpt = messageRepository.findById(message.getMessageId());
        if (messageOpt.isPresent()) {
            Message newMessage = messageOpt.get();
            if (newMessage.getSender().equals(user)) {
                newMessage.setText(message.getNewText());
                newMessage.setEditedAt(LocalDateTime.now());
                messageRepository.save(newMessage);
            }
        }
    }

    /** Удалить сообщение только если пользователь является участником чата **/
    public void deleteMessage(Integer messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            if (message.getChat().getParticipants().contains(message.getSender())) {
                messageRepository.deleteById(messageId);
            }
        }
    }

    /** Получить сообщение по ID только если пользователь является участником чата **/
    @Transactional(readOnly = true)
    public Optional<Message> getMessageById(Integer messageId, User user) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent() && messageOpt.get().getChat().getParticipants().contains(user)) {
            return messageOpt;
        }
        return Optional.empty();
    }

    /** Получить сообщения чата с пагинацией в виде списка DTO **/
    @Transactional(readOnly = true)
    public List<MessageSendersDto> getMessagesByChat(Optional<ChatWithoutMessages> chatWithoutMessages, User user, int page, int size) {
        if (chatWithoutMessages.isEmpty()) {
            return List.of();
        }

        boolean isParticipant = chatWithoutMessages.get().getParticipants().stream()
                .anyMatch(participant -> participant.getId() == user.getId());
        if (!isParticipant) {
            return List.of();
        }

        Chat chat = new Chat(chatWithoutMessages.get().getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findByChatOrderBySentAtDesc(chat, pageable);
        return convertToMessageSendersDto(messagePage.getContent());
    }

    /** Найти сообщения другого пользователя в конкретном чате **/
    @Transactional(readOnly = true)
    public List<MessageSendersDto> getUserMessagesBySender(Optional<ChatWithoutMessages> chatWithoutMessages, Optional<User> senderOpt) {
        int chatId = chatWithoutMessages.get().getId();

        if (!isParticipant(chatWithoutMessages, senderOpt)) {
            return List.of();
        }

        return convertToMessageSendersDto(messageRepository.findBySenderAndChatInOrderBySentAtDesc(senderOpt.get(), List.of(new Chat(chatId))));
    }

    /** Найти ответы на сообщение только если пользователь является участником чата **/
    @Transactional(readOnly = true)
    public Optional<List<MessageRepliesDto>> getRepliesToMessage(int parentMessageId) {
        return Optional.ofNullable(messageRepository.findRepliesDtoByParentMessageId(parentMessageId));
    }

    /** Найти сообщения по тексту только если пользователь является участником чата **/
    @Transactional(readOnly = true)
    public List<MessageSendersDto> searchMessagesByText(String text, Optional<ChatWithoutMessages> chatWithoutMessages, Optional<User> userOpt) {
        int chatId = chatWithoutMessages.get().getId();

        if (!isParticipant(chatWithoutMessages, userOpt)) {
            return List.of();
        }

        Chat chat = new Chat(chatId);
        return convertToMessageSendersDto(messageRepository.findByTextContainingIgnoreCaseAndChatOrderBySentAtDesc(text, chat));
    }

    private List<MessageSendersDto> convertToMessageSendersDto (List<Message> messages){
        List<MessageSendersDto> dto = new LinkedList<>();
        for (Message message : messages) {
            MessageSendersDto dtoMessage = new MessageSendersDto();
            dtoMessage.setId(message.getId());
            dtoMessage.setText(message.getText());
            dtoMessage.setSenderName(message.getSender().getUsername());
            dtoMessage.setSenderId(message.getSender().getId());
            dto.add(dtoMessage);
        };
        return dto;
    }

    private boolean isParticipant(Optional<ChatWithoutMessages> chatOpt, Optional<User> userOpt){
        boolean isParticipant = chatOpt.get().getParticipants().stream()
                .anyMatch(u -> u.getId() == userOpt.get().getId());
        return isParticipant;
    }
} 