package ru.sozvon.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sozvon.db.dto.modelsDTOs.UserByUsernameDto;
import ru.sozvon.db.dto.modelsDTOs.UserLoginDto;
import ru.sozvon.db.models.Chat;
import ru.sozvon.db.models.User;
import ru.sozvon.db.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Получить пользователя по ID **/
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    /** Получить пользователя по логину **/
    @Transactional(readOnly = true)
    public Optional<User> getUserByLogin(UserLoginDto login) {
        Optional<User> user = userRepository.findByLogin(login.getLogin());
        if (user.isPresent()) {
            if (user.get().getLogin().equals(login.getLogin())) {
                return user;
            }
        }
        return Optional.empty();
    }

    /** Получить пользователя по имени пользователя **/
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** Получить все чаты пользователя **/
    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return userOpt.get().getChats();
        }
        return Collections.emptyList();
    }

    /** Сохранить пользователя **/
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /** Удалить пользователя **/
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    /** Обновить пользователя **/
    public void updateUser(UserByUsernameDto user) {
        Optional<User> userOpt = userRepository.findById(user.getId());
        if (userOpt.isPresent()) {
            User updatedUser = userOpt.get();
            updatedUser.setUsername(user.getUsername());
            userRepository.save(updatedUser);
        }
    }
}