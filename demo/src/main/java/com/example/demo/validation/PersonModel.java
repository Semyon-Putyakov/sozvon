package com.example.demo.validation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonModel {
    @NotEmpty(message = "Пустой логин")
    @Size(min = 3, max = 100, message = "Минимальное количество символов - 3, Максимальное количество символов - 100")
    private String username;
    @NotEmpty(message = "Пустой пароль")
    @Size(min = 3, max = 100, message = "Минимальное количество символов - 3, Максимальное количество символов - 100")
    private String password;

    private int id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
