package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public  class PasswordEncoding {

    private static PasswordEncoder passwordEncoder = null;

    @Autowired
    public PasswordEncoding(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
