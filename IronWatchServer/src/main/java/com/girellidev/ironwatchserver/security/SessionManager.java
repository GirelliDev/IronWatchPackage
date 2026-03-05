package com.girellidev.ironwatchserver.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static int expirationMinutes = 60;

    public static Session createSession(String userId, String role) {

        String token = UUID.randomUUID().toString();
        long expirationTime = calculateExpiration();

        Session session = new Session(token, role, expirationTime);

        sessions.put(token, session);

        return session;
    }

    public static Session getSession(String token) {

        Session session = sessions.get(token);

        if (session == null) return null;

        if (session.isExpired()) {
            sessions.remove(token);
            return null;
        }

        return session;
    }

    public static boolean validateSession(String token) {
        return getSession(token) != null;
    }

    public static void removeSession(String token) {
        sessions.remove(token);
    }

    public static void setExpirationMinutes(int minutes) {
        expirationMinutes = minutes;
    }

    public static int getExpirationMinutes() {
        return expirationMinutes;
    }

    public static long calculateExpiration() {
        return System.currentTimeMillis() + (expirationMinutes * 60_000L);
    }
    
    public static long calculateExpiration(int minutes) {
    return System.currentTimeMillis() + (minutes * 60_000L);
}
}