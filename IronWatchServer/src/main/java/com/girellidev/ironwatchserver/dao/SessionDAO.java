package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

import com.girellidev.ironwatchserver.model.Session;

public class SessionDAO {

    private static final String INSERT_SESSION_SQL = """
            INSERT INTO sessoes_admin (usuario_id, token, expira_em, ativo)
            VALUES (?, ?, ?, ?)
            """;

    private static final String FIND_BY_TOKEN_SQL = """
            SELECT id, usuario_id, token, expira_em, ativo
            FROM sessoes_admin
            WHERE token = ?
            LIMIT 1
            """;

    private static final String IS_SESSION_VALID_SQL = """
            SELECT expira_em, ativo
            FROM sessoes_admin
            WHERE token = ?
            LIMIT 1
            """;

    private static final String DEACTIVATE_SESSION_SQL = """
            UPDATE sessoes_admin
            SET ativo = false
            WHERE token = ?
            """;

    public boolean insertSession(Session session) throws SQLException {
        validateSession(session);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_SESSION_SQL)
        ) {
            bindInsert(ps, session);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException(
                    "Erro ao inserir sessao. usuarioId=" + session.getUsuarioId(),
                    e
            );
        }
    }

    public Session findByToken(String token) throws SQLException {
        validateToken(token);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(FIND_BY_TOKEN_SQL)
        ) {
            ps.setString(1, normalize(token));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar sessao por token.", e);
        }
    }

    public boolean isSessionValid(String token) throws SQLException {
        validateToken(token);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(IS_SESSION_VALID_SQL)
        ) {
            ps.setString(1, normalize(token));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresAt = rs.getTimestamp("expira_em");
                    boolean ativo = rs.getBoolean("ativo");

                    return ativo
                            && expiresAt != null
                            && expiresAt.toLocalDateTime().isAfter(LocalDateTime.now());
                }
            }

            return false;

        } catch (SQLException e) {
            throw new SQLException("Erro ao validar sessao por token.", e);
        }
    }

    public boolean deactivateSession(String token) throws SQLException {
        validateToken(token);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(DEACTIVATE_SESSION_SQL)
        ) {
            ps.setString(1, normalize(token));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException("Erro ao desativar sessao por token.", e);
        }
    }

    private void bindInsert(PreparedStatement ps, Session session) throws SQLException {
        ps.setInt(1, session.getUsuarioId());
        ps.setString(2, normalize(session.getToken()));
        ps.setTimestamp(3, Timestamp.valueOf(session.getExpiraEm()));
        ps.setBoolean(4, session.isAtivo());
    }

    private Session mapResultSet(ResultSet rs) throws SQLException {
        Session session = new Session();

        session.setId(rs.getInt("id"));
        session.setUsuarioId(rs.getInt("usuario_id"));
        session.setToken(normalize(rs.getString("token")));

        Timestamp expiraEm = rs.getTimestamp("expira_em");
        if (expiraEm != null) {
            session.setExpiraEm(expiraEm.toLocalDateTime());
        }

        session.setAtivo(rs.getBoolean("ativo"));

        return session;
    }

    private void validateSession(Session session) {
        Objects.requireNonNull(session, "Session não pode ser nulo.");

        if (session.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("usuarioId inválido.");
        }

        validateToken(session.getToken());

        if (session.getExpiraEm() == null) {
            throw new IllegalArgumentException("expiraEm não pode ser nulo.");
        }
    }

    private void validateToken(String token) {
        if (isBlank(token)) {
            throw new IllegalArgumentException("Token não pode ser vazio.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}