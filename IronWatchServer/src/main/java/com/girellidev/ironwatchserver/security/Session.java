package com.girellidev.ironwatchserver.security;

public class Session {

    private final String token;
    private final String role;
    private final long expiration;

    public Session(String token, String role, long expiration) {
        this.token = token;
        this.role = role;
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public long getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiration;
    }
}