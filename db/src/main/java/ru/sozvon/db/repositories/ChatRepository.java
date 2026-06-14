package ru.sozvon.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.User;
import ru.sozvon.db.dto.modelsDTOs.ChatWithIdAndTitleDto;
import ru.sozvon.db.dto.modelsDTOs.UserByUsernameDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    /** Получить чаты конкретного пользователя только с id и title **/
    @Query("SELECT new ru.sozvon.db.dto.modelsDTOs.ChatWithIdAndTitleDto(c.id, c.title) FROM Chat c JOIN c.participants p WHERE p = :user")
    List<ChatWithIdAndTitleDto> findChatsWithIdAndTitleByUser(@Param("user") User user);

    /** Получить участников конкретного чата только с id и username **/
    @Query("SELECT new ru.sozvon.db.dto.modelsDTOs.UserByUsernameDto(u.id, u.username) FROM Chat c JOIN c.participants u WHERE c.id = :chatId")
    List<UserByUsernameDto> findParticipantsByChatId(@Param("chatId") int chatId);

    /** Получить чат по id только с id и title **/
    @Query("SELECT new ru.sozvon.db.dto.modelsDTOs.ChatWithIdAndTitleDto(c.id, c.title) FROM Chat c WHERE c.id = :chatId")
    Optional<ChatWithIdAndTitleDto> findChatWithIdAndTitleById(@Param("chatId") int chatId);

} 