package ru.sozvon.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sozvon.db.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /** Найти пользователя по логину **/
    Optional<User> findByLogin(String login);

    /** Найти пользователя по имени пользователя **/
    Optional<User> findByUsername(String username);

} 