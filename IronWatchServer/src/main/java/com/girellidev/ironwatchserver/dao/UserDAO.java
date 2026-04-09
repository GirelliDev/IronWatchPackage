package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.girellidev.ironwatchserver.model.User;

public class UserDAO {

    private static final String INSERT_USER_SQL = """
            INSERT INTO usuarios (login, password_hash, role, empresa_id, active)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_LOGIN_SQL = """
            SELECT id, login, password_hash, role, empresa_id, active
            FROM usuarios
            WHERE login = ?
            LIMIT 1
            """;

    private static final String GET_PASSWORD_HASH_SQL = """
            SELECT password_hash
            FROM usuarios
            WHERE login = ? AND active = 1
            LIMIT 1
            """;

    private static final String GET_USER_ID_SQL = """
            SELECT id
            FROM usuarios
            WHERE login = ? AND active = 1
            LIMIT 1
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, login, password_hash, role, empresa_id, active
            FROM usuarios
            WHERE id = ?
            LIMIT 1
            """;

    public boolean insertUser(User user) throws SQLException {
        validateUser(user);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL)
        ) {
            bindInsert(ps, user);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException(
                    "Erro ao inserir usuario. login=" + user.getLogin()
                            + ", empresaId=" + user.getEmpresaId()
                            + ", role=" + user.getRole(),
                    e
            );
        }
    }

    public User findByLogin(String login) throws SQLException {
        validateLogin(login);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(FIND_BY_LOGIN_SQL)
        ) {
            ps.setString(1, normalize(login));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar usuario por login.", e);
        }
    }

    public String getPasswordHash(String login) throws SQLException {
        validateLogin(login);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_PASSWORD_HASH_SQL)
        ) {
            ps.setString(1, normalize(login));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return normalize(rs.getString("password_hash"));
                }
            }

            return null;

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar password_hash do usuario.", e);
        }
    }

    public int getUserId(String login) throws SQLException {
        validateLogin(login);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_USER_ID_SQL)
        ) {
            ps.setString(1, normalize(login));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            return -1;

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar ID do usuario por login.", e);
        }
    }

    public User findById(int id) throws SQLException {
        validateUserId(id);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(FIND_BY_ID_SQL)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar usuario por ID. id=" + id, e);
        }
    }

    private void bindInsert(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, normalize(user.getLogin()));
        ps.setString(2, normalize(user.getPasswordHash()));
        ps.setInt(3, user.getRole());
        ps.setInt(4, user.getEmpresaId());
        ps.setBoolean(5, user.isActive());
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                normalize(rs.getString("login")),
                normalize(rs.getString("password_hash")),
                rs.getInt("role"),
                rs.getInt("empresa_id"),
                rs.getBoolean("active")
        );
    }

    private void validateUser(User user) {
        Objects.requireNonNull(user, "User não pode ser nulo.");

        validateLogin(user.getLogin());

        if (isBlank(user.getPasswordHash())) {
            throw new IllegalArgumentException("passwordHash não pode ser vazio.");
        }

        if (user.getEmpresaId() <= 0) {
            throw new IllegalArgumentException("empresaId inválido.");
        }

        if (user.getRole() < 0) {
            throw new IllegalArgumentException("role inválido.");
        }
    }

    private void validateLogin(String login) {
        if (isBlank(login)) {
            throw new IllegalArgumentException("Login não pode ser vazio.");
        }
    }

    private void validateUserId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}