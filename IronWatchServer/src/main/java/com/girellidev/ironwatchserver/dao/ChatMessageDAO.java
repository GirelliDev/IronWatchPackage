package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.girellidev.ironwatchserver.model.ChatMessage;

public class ChatMessageDAO {

    public boolean insert(ChatMessage message) throws SQLException {
        String sql = """
                INSERT INTO chat_message (empresa_id, usuario_id, role, content)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, message.getEmpresaId());
            ps.setInt(2, message.getUsuarioId());
            ps.setString(3, message.getRole());
            ps.setString(4, message.getContent());

            return ps.executeUpdate() > 0;
        }
    }
}