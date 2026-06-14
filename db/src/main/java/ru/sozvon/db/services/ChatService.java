package ru.sozvon.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sozvon.db.dto.modelsDTOs.ChatWithIdAndTitleDto;
import ru.sozvon.db.dto.modelsDTOs.ChatWithoutMessages;
import ru.sozvon.db.dto.modelsDTOs.UserByUsernameDto;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;
import ru.sozvon.db.repositories.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;

    @Autowired
    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    /** Получить чат по ID только если пользователь является участником **/
    @Transactional(readOnly = true)
    public Optional<ChatWithoutMessages> getChatByIdGetDTO(Integer chatId) {
        List<UserByUsernameDto> users = chatRepository.findParticipantsByChatId(chatId);
        Optional<ChatWithIdAndTitleDto> chat = chatRepository.findChatWithIdAndTitleById(chatId);

        if (chat.isPresent()) {
            ChatWithoutMessages chatWithoutMessages = new ChatWithoutMessages();
            chatWithoutMessages.setTitle(chat.get().getTitle());
            chatWithoutMessages.setParticipants(users);
            chatWithoutMessages.setId(chat.get().getId());
            return Optional.of(chatWithoutMessages);
        } else {
            return Optional.empty();
        }
    }

    /** Получить все чаты в которых пользователь состоит **/
    @Transactional(readOnly = true)
    public List<ChatWithIdAndTitleDto> getUserChatsWithoutMessages(User user) {
        return chatRepository.findChatsWithIdAndTitleByUser(user);
    }

    /** Получить все сообщения чата только если пользователь является участником **/
    @Transactional(readOnly = true)
    public Optional<List<Message>> getAllChatMessages(Integer chatId, User user) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            if (chat.getParticipants().contains(user)) {
                return Optional.of(chat.getMessages());
            }
        }
        return Optional.empty();
    }

    /** Найти чаты по названию в которых пользователь состоит **/
    @Transactional(readOnly = true)
    public Optional<ChatWithIdAndTitleDto> findUserChatsByTitle(String title, User user) {
        List<ChatWithIdAndTitleDto> userChats = chatRepository.findChatsWithIdAndTitleByUser(user);

        for (ChatWithIdAndTitleDto chat : userChats) {
            if (chat.getTitle() != null
                    && chat.getTitle().replaceAll("\\s+", "")
                    .equalsIgnoreCase(title.replaceAll("\\s+", ""))) {
                return Optional.of(chat);
            }
        }
        return Optional.empty();
    }

    /** Удалить чат только если пользователь является участником **/
    public void deleteChat(Integer chatId, User user) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isPresent() && chat.get().getParticipants().contains(user)) {
            chatRepository.deleteById(chatId);
        }
    }

    /** Создать чат **/
    public void createChat(Chat chat) {
        chatRepository.save(chat);
    }

    /** Добавить участника в чат **/
    public boolean addParticipantToChat(int chatId, int userId, int participantId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            if (chat.getParticipants().stream().anyMatch(u -> u.getId() == userId)) {
                Optional<User> participantOpt = userService.getUserById(participantId);
                if (participantOpt.isPresent()) {
                    User participant = participantOpt.get();
                    if (!chat.getParticipants().contains(participant)) {
                        chat.getParticipants().add(participant);
                        chatRepository.save(chat);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Удалить участника из чата **/
    public boolean removeParticipantFromChat(int chatId, int userId, int participantId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            if (chat.getParticipants().stream().anyMatch(u -> u.getId() == userId)) {
                boolean removed = chat.getParticipants().removeIf(u -> u.getId() == participantId);
                if (removed) {
                    chatRepository.save(chat);
                    return true;
                }
            }
        }
        return false;
    }

    /** Получить список участников чата **/
    @Transactional(readOnly = true)
    public List<UserByUsernameDto> getChatParticipants(int chatId) {
        return chatRepository.findParticipantsByChatId(chatId);
    }
}