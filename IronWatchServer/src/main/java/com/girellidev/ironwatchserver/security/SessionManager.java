package com.girellidev.ironwatchserver.security;

import java.time.LocalDateTime;

public class SessionManager {

    public static LocalDateTime expirationMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    public static boolean isExpired(LocalDateTime expiration) {
        return LocalDateTime.now().isAfter(expiration);
    }
}