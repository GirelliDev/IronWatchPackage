package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.girellidev.ironwatchserver.model.Session;

public class SessionDAO {

    public boolean insertSession(Session session) throws SQLException {

        String sql =
                "INSERT INTO sessoes_admin (usuario_id, token, expira_em, ativo) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, session.getUsuarioId());
            ps.setString(2, session.getToken());
            ps.setTimestamp(3, Timestamp.valueOf(session.getExpiraEm()));
            ps.setBoolean(4, session.isAtivo());

            return ps.executeUpdate() > 0;
        }
    }
    public Session findByToken(String token) throws SQLException {

    String sql =
            "SELECT id, usuario_id, token, expira_em, ativo FROM sessoes_admin WHERE token = ? LIMIT 1";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, token);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Session session = new Session();

                session.setId(rs.getInt("id"));
                session.setUsuarioId(rs.getInt("usuario_id"));
                session.setToken(rs.getString("token"));
                session.setExpiraEm(rs.getTimestamp("expira_em").toLocalDateTime());
                session.setAtivo(rs.getBoolean("ativo"));

                return session;
            }
        }
    }

    return null;
}

    public boolean isSessionValid(String token) throws SQLException {

        String sql =
                "SELECT expira_em, ativo FROM sessoes_admin WHERE token = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Timestamp ts = rs.getTimestamp("expira_em");
                    boolean ativo = rs.getBoolean("ativo");

                    return ativo && ts.toLocalDateTime().isAfter(LocalDateTime.now());
                }
            }
        }

        return false;
    }

 
}