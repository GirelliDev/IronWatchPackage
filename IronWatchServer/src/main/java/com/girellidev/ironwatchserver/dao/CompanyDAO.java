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
                SELECT id, nome, is_active
                FROM empresas
                ORDER BY nome ASC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                int isActive = resultSet.getInt("is_active");

                companies.add(new CompanyStatusDTO(id, nome, isActive));
            }
        }

        return companies;
    }

    public int createCompany(
            String nome,
            String razaoSocial,
            String telefone,
            String email,
            String promptIa,
            String endereco,
            int dispositivosMax,
            int isActive
    ) throws Exception {
        String sql = """
                INSERT INTO empresas
                (nome, razaosocial, telefone, email, promptia, endereco, dispositivos_max, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, nome);
            statement.setString(2, razaoSocial);
            statement.setString(3, telefone);
            statement.setString(4, email);
            statement.setString(5, promptIa);
            statement.setString(6, endereco);
            statement.setInt(7, dispositivosMax);
            statement.setInt(8, isActive);

            int rows = statement.executeUpdate();
            if (rows <= 0) {
                throw new Exception("Falha ao criar empresa.");
            }

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new Exception("Nao foi possivel obter o ID da empresa criada.");
        }
    }

    public boolean updateCompany(
            int companyId,
            String nome,
            String razaoSocial,
            String telefone,
            String email,
            String promptIa,
            String endereco,
            int dispositivosMax,
            int isActive
    ) throws Exception {
        String sql = """
                UPDATE empresas
                SET nome = ?,
                    razaosocial = ?,
                    telefone = ?,
                    email = ?,
                    promptia = ?,
                    endereco = ?,
                    dispositivos_max = ?,
                    is_active = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, nome);
            statement.setString(2, razaoSocial);
            statement.setString(3, telefone);
            statement.setString(4, email);
            statement.setString(5, promptIa);
            statement.setString(6, endereco);
            statement.setInt(7, dispositivosMax);
            statement.setInt(8, isActive);
            statement.setInt(9, companyId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteCompany(int companyId) throws Exception {
        String sql = "DELETE FROM empresas WHERE id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, companyId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean setCompanyActive(int companyId, int isActive) throws Exception {
        String sql = """
                UPDATE empresas
                SET is_active = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, isActive);
            statement.setInt(2, companyId);
            return statement.executeUpdate() > 0;
        }
    }

    public String getCompanyNameById(int companyId) throws Exception {
        String sql = "SELECT nome FROM empresas WHERE id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nome");
                }
            }
        }

        return null;
    }
}