package com.girellidev.ironwatchserver.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        try {
            // Configura sua conexão com MySQL
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/GDS_IronWatch?useSSL=false&serverTimezone=UTC",
                "root",
                ""
            );
        } catch (SQLException e) {
        }
    }

    // Insert user
    public boolean insertUser(String login, String hashedPassword, int role, int empresaId) throws SQLException {
        String sql = "INSERT INTO usuarios (login, password, role, empresa_id, active) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, hashedPassword);
            ps.setInt(3, role);
            ps.setInt(4, empresaId);
            return ps.executeUpdate() > 0;
        }
    }

    // Get hash
    public String getPasswordHash(String login) throws SQLException {
        String sql = "SELECT password FROM usuarios WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("password");
            return null;
        }
    }

    // Get userId
    public int getUserId(String login) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return -1;
        }
    }

    // Insert session
    public boolean insertSession(int userId, String token, LocalDateTime expiraEm) throws SQLException {
        String sql = "INSERT INTO sessoes_admin (usuario_id, token, expira_em) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(expiraEm));
            return ps.executeUpdate() > 0;
        }
    }

    // Validate session
    public boolean isSessionValid(String token) throws SQLException {
        String sql = "SELECT id, expira_em, ativo FROM sessoes_admin WHERE token = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("expira_em");
                boolean ativo = rs.getBoolean("ativo");
                return ativo && ts.toLocalDateTime().isAfter(LocalDateTime.now());
            }
            return false;
        }
    }
}