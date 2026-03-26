package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CompanyAIConfigDAO {

    public boolean createAIConfig(
            int empresaId,
            String provider,
            String apiKeyEncrypted,
            String model,
            int active
    ) throws Exception {
        String sql = """
                INSERT INTO empresa_ai_config
                (empresa_id, provider, api_key_encrypted, model, active)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, empresaId);
            statement.setString(2, provider);
            statement.setString(3, apiKeyEncrypted);
            statement.setString(4, model);
            statement.setInt(5, active);

            return statement.executeUpdate() > 0;
        }
    }
}