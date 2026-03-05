package com.girellidev.ironwatchserver.security;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SecurityManager {

    private final UserDAO userDAO;

    public SecurityManager() {
        this.userDAO = new UserDAO(); // inicializa conexão com banco
    }

    // Criar usuário com hash
    public boolean createUser(String login, String password, int role, int empresaId) {
        String hashed = PasswordHasher.hash(password);
        try {
            return userDAO.insertUser(login, hashed, role, empresaId);
        } catch (SQLException e) {
            return false;
        }
    }

    // Validar login
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean validateLogin(String login, String password) {
        try {
            String hash = userDAO.getPasswordHash(login);
            if (hash == null) return false;
            return PasswordHasher.verify(password, hash);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Gerar token de sessão e salvar no banco
  public String createSession(String login) {
    String token = TokenGenerator.generateSessionToken();
    try {
        int userId = userDAO.getUserId(login);
        if (userId == -1) return null;

        LocalDateTime expiration =
                Instant.ofEpochMilli(SessionManager.calculateExpiration(30))
                       .atZone(ZoneId.systemDefault())
                       .toLocalDateTime();

        userDAO.insertSession(userId, token, expiration);

        return token;
    } catch (SQLException e) {
        return null;
    }
}

    // Validar token de sessão
    public boolean validateSession(String token) {
        try {
            return userDAO.isSessionValid(token);
        } catch (SQLException e) {
            return false;
        }
    }

}