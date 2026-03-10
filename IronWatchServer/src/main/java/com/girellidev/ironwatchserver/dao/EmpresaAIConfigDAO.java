package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.girellidev.ironwatchserver.model.EmpresaAIConfig;

public class EmpresaAIConfigDAO {

    public EmpresaAIConfig findActiveByEmpresaId(int empresaId) throws SQLException {
        String sql = """
                SELECT id, empresa_id, provider, api_key_encrypted, model, active, created_at, updated_at
                FROM empresa_ai_config
                WHERE empresa_id = ? AND active = 1
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, empresaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmpresaAIConfig config = new EmpresaAIConfig();
                    config.setId(rs.getInt("id"));
                    config.setEmpresaId(rs.getInt("empresa_id"));
                    config.setProvider(rs.getString("provider"));
                    config.setApiKeyEncrypted(rs.getString("api_key_encrypted"));
                    config.setModel(rs.getString("model"));
                    config.setActive(rs.getBoolean("active"));
                    return config;
                }
            }
        }

        return null;
    }

    public boolean insert(EmpresaAIConfig config) throws SQLException {
        String sql = """
                INSERT INTO empresa_ai_config
                (empresa_id, provider, api_key_encrypted, model, active)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, config.getEmpresaId());
            ps.setString(2, config.getProvider());
            ps.setString(3, config.getApiKeyEncrypted());
            ps.setString(4, config.getModel());
            ps.setBoolean(5, config.isActive());

            return ps.executeUpdate() > 0;
        }
    }
}