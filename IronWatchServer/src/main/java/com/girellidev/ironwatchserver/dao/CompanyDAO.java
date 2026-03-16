package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.girellidev.ironwatchserver.dto.CompanyStatusDTO;

public class CompanyDAO {

    public List<CompanyStatusDTO> listAllCompanies() throws Exception {
        List<CompanyStatusDTO> companies = new ArrayList<>();

        String sql = """
                SELECT Nome, is_active
                FROM empresas
                ORDER BY Nome ASC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                String nome = resultSet.getString("Nome");
                int isActive = resultSet.getInt("is_active");

                companies.add(new CompanyStatusDTO(nome, isActive));
            }
        }

        return companies;
    }
}