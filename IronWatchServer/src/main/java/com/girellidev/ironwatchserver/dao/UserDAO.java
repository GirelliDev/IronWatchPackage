package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.girellidev.ironwatchserver.model.User;

public class UserDAO {

    public boolean insertUser(User user) throws SQLException {

        String sql =
                "INSERT INTO usuarios (login, password_hash, role, empresa_id, active) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setInt(3, user.getRole());
            ps.setInt(4, user.getEmpresaId());
            ps.setBoolean(5, user.isActive());

            return ps.executeUpdate() > 0;
        }
    }

    public User findByLogin(String login) throws SQLException {

        String sql =
                "SELECT id, login, password_hash, role, empresa_id, active FROM usuarios WHERE login = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public String getPasswordHash(String login) throws SQLException {

        String sql =
                "SELECT password_hash FROM usuarios WHERE login = ? AND active = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }

        return null;
    }

    public int getUserId(String login) throws SQLException {

        String sql =
                "SELECT id FROM usuarios WHERE login = ? AND active = 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return -1;
    }
    public User findById(int id) throws SQLException {

    String sql =
            "SELECT id, login, password_hash, role, empresa_id, active FROM usuarios WHERE id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapUser(rs);
            }
        }
    }

    return null;
}

    private User mapUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("password_hash"),
                rs.getInt("role"),
                rs.getInt("empresa_id"),
                rs.getBoolean("active")
        );
    }
}