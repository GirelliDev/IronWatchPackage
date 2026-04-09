package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class CompanyAIConfigDAO {

    private static final String INSERT_SQL = """
            INSERT INTO empresa_ai_config
            (empresa_id, provider, api_key_encrypted, model, active)
            VALUES (?, ?, ?, ?, ?)
            """;

    public boolean createAIConfig(
            int empresaId,
            String provider,
            String apiKeyEncrypted,
            String model,
            int active
    ) throws SQLException {

        validate(empresaId, provider, apiKeyEncrypted, model, active);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL)
        ) {
            bind(statement, empresaId, provider, apiKeyEncrypted, model, active);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new SQLException(
                    "Erro ao criar configuração de IA. empresa_id=" + empresaId +
                    ", provider=" + provider +
                    ", model=" + model,
                    e
            );
        }
    }

    private void bind(
            PreparedStatement statement,
            int empresaId,
            String provider,
            String apiKeyEncrypted,
            String model,
            int active
    ) throws SQLException {

        statement.setInt(1, empresaId);
        statement.setString(2, normalize(provider));
        statement.setString(3, normalize(apiKeyEncrypted));
        statement.setString(4, normalize(model));
        statement.setInt(5, active);
    }

    private void validate(
            int empresaId,
            String provider,
            String apiKeyEncrypted,
            String model,
            int active
    ) {

        if (empresaId <= 0) {
            throw new IllegalArgumentException("empresa_id inválido.");
        }

        if (isBlank(provider)) {
            throw new IllegalArgumentException("provider não pode ser vazio.");
        }

        if (isBlank(apiKeyEncrypted)) {
            throw new IllegalArgumentException("apiKeyEncrypted não pode ser vazio.");
        }

        if (isBlank(model)) {
            throw new IllegalArgumentException("model não pode ser vazio.");
        }

        if (active != 0 && active != 1) {
            throw new IllegalArgumentException("active deve ser 0 ou 1.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}