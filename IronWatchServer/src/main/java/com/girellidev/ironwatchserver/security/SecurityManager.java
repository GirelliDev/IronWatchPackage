package com.girellidev.ironwatchserver.security;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.girellidev.ironwatchserver.dao.SessionDAO;
import com.girellidev.ironwatchserver.dao.UserDAO;
import com.girellidev.ironwatchserver.model.Session;
import com.girellidev.ironwatchserver.model.User;

public class SecurityManager {

    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;

    public SecurityManager() {
        this.userDAO = new UserDAO();
        this.sessionDAO = new SessionDAO();
    }

    public boolean createUser(String login, String password, int role, int empresaId) {
        String hashed = PasswordHasher.hash(password);

        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(hashed);
        user.setRole(role);
        user.setEmpresaId(empresaId);
        user.setActive(true);

        try {
            return userDAO.insertUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateLogin(String login, String password) {
        try {
            String hash = userDAO.getPasswordHash(login);

            if (hash == null) {
                return false;
            }

            return PasswordHasher.verify(password, hash);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createSession(String login) {
        String token = TokenGenerator.generateSessionToken();

        try {
            int userId = userDAO.getUserId(login);

            if (userId == -1) {
                return null;
            }

            LocalDateTime expiration =
                    Instant.ofEpochMilli(SessionManager.calculateExpiration(30))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

            Session session = new Session();
            session.setUsuarioId(userId);
            session.setToken(token);
            session.setExpiraEm(expiration);
            session.setAtivo(true);

            boolean inserted = sessionDAO.insertSession(session);

            return inserted ? token : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateSession(String token) {
        try {
            return sessionDAO.isSessionValid(token);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByLogin(String login) {
        try {
            return userDAO.findByLogin(login);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByToken(String token) {
        try {
            Session session = sessionDAO.findByToken(token);

            if (session == null) {
                return null;
            }

            if (!session.isAtivo()) {
                return null;
            }

            if (session.getExpiraEm() == null || session.getExpiraEm().isBefore(LocalDateTime.now())) {
                return null;
            }

            return userDAO.findById(session.getUsuarioId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}