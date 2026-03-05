package com.girellidev.ironwatchserver.security;

import java.time.LocalDateTime;

public class AuthCode {

    private final String code;
    private final CodeType type;
    private final LocalDateTime expiration;

    public AuthCode(String code, CodeType type, LocalDateTime expiration) {
        this.code = code;
        this.type = type;
        this.expiration = expiration;
    }

    public String getCode() { return code; }
    public CodeType getType() { return type; }
    public LocalDateTime getExpiration() { return expiration; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}