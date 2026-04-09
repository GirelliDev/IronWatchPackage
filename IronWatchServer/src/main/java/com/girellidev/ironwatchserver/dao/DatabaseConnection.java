package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String HOST = getEnv("DB_HOST", "localhost");
    private static final String PORT = getEnv("DB_PORT", "3306");
    private static final String DATABASE = getEnv("DB_NAME", "gds_ironwatch");
    private static final String USER = getEnv("DB_USER", "ironwatch");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "DNMQTDC");
    private static final String USE_SSL = getEnv("DB_USE_SSL", "false");

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=5000",
            HOST,
            PORT,
            DATABASE,
            USE_SSL
    );

    static {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("[DB] Driver MySQL carregado.");

            System.out.println("[DB] Configuracao:");
            System.out.println("[DB] HOST=" + HOST);
            System.out.println("[DB] PORT=" + PORT);
            System.out.println("[DB] DATABASE=" + DATABASE);

        } catch (ClassNotFoundException e) {

            throw new RuntimeException("Driver MySQL nao encontrado.", e);

        }

    }

    private DatabaseConnection() {
        throw new IllegalStateException("Classe utilitaria.");
    }

    public static Connection getConnection() throws SQLException {

        try {

            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection == null || connection.isClosed()) {
                throw new SQLException("Nao foi possivel abrir conexao com o banco.");
            }

            return connection;

        } catch (SQLException e) {

            throw new SQLException(
                    "Falha ao conectar no banco. HOST=" + HOST +
                    " PORT=" + PORT +
                    " DATABASE=" + DATABASE,
                    e
            );

        }

    }

    private static String getEnv(String key, String defaultValue) {

        String value = System.getenv(key);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value.trim();

    }

}