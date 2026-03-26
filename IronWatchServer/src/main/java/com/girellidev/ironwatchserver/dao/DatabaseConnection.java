package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String HOST = getEnv("DB_HOST", "localhost");
    private static final String PORT = getEnv("DB_PORT", "3306");
    private static final String DATABASE = getEnv("DB_NAME", "gds_ironwatch");
    private static final String USER = getEnv("DB_USER", "ironwatch");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "");
    private static final String USE_SSL = getEnv("DB_USE_SSL", "true");

    private static final String URL = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=%s&serverTimezone=UTC&allowPublicKeyRetrieval=false",
            HOST, PORT, DATABASE, USE_SSL
    );

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL nao encontrado.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}