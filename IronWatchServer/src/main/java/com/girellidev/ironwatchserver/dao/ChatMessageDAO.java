package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import com.girellidev.ironwatchserver.model.ChatMessage;

public class ChatMessageDAO {

    private static final String INSERT_SQL = """
            INSERT INTO chat_message (empresa_id, usuario_id, role, content)
            VALUES (?, ?, ?, ?)
            """;

    public boolean insert(ChatMessage message) throws SQLException {
        validateMessage(message);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {

            bindInsertParameters(ps, message);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException(
                    "Erro ao inserir mensagem no chat. empresa_id=" + message.getEmpresaId()
                            + ", usuario_id=" + message.getUsuarioId()
                            + ", role=" + message.getRole(),
                    e
            );
        }
    }

    private void bindInsertParameters(PreparedStatement ps, ChatMessage message) throws SQLException {
        ps.setInt(1, message.getEmpresaId());
        ps.setInt(2, message.getUsuarioId());
        ps.setString(3, normalize(message.getRole()));
        ps.setString(4, normalize(message.getContent()));
    }

    private void validateMessage(ChatMessage message) {
        Objects.requireNonNull(message, "ChatMessage não pode ser nulo.");

        if (message.getEmpresaId() <= 0) {
            throw new IllegalArgumentException("empresa_id inválido.");
        }

        if (message.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("usuario_id inválido.");
        }

        if (isBlank(message.getRole())) {
            throw new IllegalArgumentException("role não pode ser vazio.");
        }

        if (isBlank(message.getContent())) {
            throw new IllegalArgumentException("content não pode ser vazio.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}