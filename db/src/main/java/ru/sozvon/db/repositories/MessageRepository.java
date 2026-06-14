package ru.sozvon.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.Message;
import ru.sozvon.db.models.User;
import ru.sozvon.db.dto.modelsDTOs.MessageRepliesDto;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    /** Найти сообщения в чате с пагинацией **/
    Page<Message> findByChatOrderBySentAtDesc(Chat chat, Pageable pageable);

    /** Найти сообщения отправителя в определенных чатах **/
    List<Message> findBySenderAndChatInOrderBySentAtDesc(User sender, List<Chat> chats);

    /** Получить список ответов (id, text) на сообщение по его id. Возвращает упорядоченный по времени отправки список.*/
    @Query("select new ru.sozvon.db.dto.modelsDTOs.MessageRepliesDto(m.id, m.text) " +
           "from Message m where m.parentMessage.id = :parentId order by m.sentAt asc")
    List<MessageRepliesDto> findRepliesDtoByParentMessageId(@Param("parentId") int parentId);

    /** Поиск сообщений по тексту в конкретном чате **/
    List<Message> findByTextContainingIgnoreCaseAndChatOrderBySentAtDesc(String text, Chat chat);

} 