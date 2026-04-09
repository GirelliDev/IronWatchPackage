package com.girellidev.ironwatchserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.girellidev.ironwatchserver.dto.CompanyStatusDTO;

public class CompanyDAO {

    private static final String LIST_ALL_SQL = """
            SELECT id, nome, is_active
            FROM empresas
            ORDER BY nome ASC
            """;

    private static final String INSERT_SQL = """
            INSERT INTO empresas
            (nome, razaosocial, telefone, email, promptia, endereco, dispositivos_max, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
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

    private static final String DELETE_SQL =
            "DELETE FROM empresas WHERE id = ?";

    private static final String SET_ACTIVE_SQL = """
            UPDATE empresas
            SET is_active = ?
            WHERE id = ?
            """;

    private static final String GET_NAME_SQL =
            "SELECT nome FROM empresas WHERE id = ?";



    public List<CompanyStatusDTO> listAllCompanies() throws SQLException {

        List<CompanyStatusDTO> companies = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(LIST_ALL_SQL);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                companies.add(
                        new CompanyStatusDTO(
                                resultSet.getInt("id"),
                                normalize(resultSet.getString("nome")),
                                resultSet.getInt("is_active")
                        )
                );

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
    ) throws SQLException {

        validateCompanyData(nome, dispositivosMax, isActive);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL,
                        PreparedStatement.RETURN_GENERATED_KEYS
                )
        ) {

            bindCompany(statement,
                    nome,
                    razaoSocial,
                    telefone,
                    email,
                    promptIa,
                    endereco,
                    dispositivosMax,
                    isActive
            );

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Falha ao criar empresa.");
            }

            try (ResultSet rs = statement.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }

            }

            throw new SQLException("Nao foi possivel obter o ID da empresa criada.");

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
    ) throws SQLException {

        validateCompanyData(nome, dispositivosMax, isActive);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)
        ) {

            bindCompany(statement,
                    nome,
                    razaoSocial,
                    telefone,
                    email,
                    promptIa,
                    endereco,
                    dispositivosMax,
                    isActive
            );

            statement.setInt(9, companyId);

            return statement.executeUpdate() > 0;
        }
    }



    public boolean deleteCompany(int companyId) throws SQLException {

        if (companyId <= 0) {
            throw new IllegalArgumentException("companyId inválido.");
        }

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL)
        ) {

            statement.setInt(1, companyId);

            return statement.executeUpdate() > 0;

        }
    }



    public boolean setCompanyActive(int companyId, int isActive) throws SQLException {

        if (companyId <= 0) {
            throw new IllegalArgumentException("companyId inválido.");
        }

        if (isActive != 0 && isActive != 1) {
            throw new IllegalArgumentException("isActive deve ser 0 ou 1.");
        }

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(SET_ACTIVE_SQL)
        ) {

            statement.setInt(1, isActive);
            statement.setInt(2, companyId);

            return statement.executeUpdate() > 0;

        }
    }



    public String getCompanyNameById(int companyId) throws SQLException {

        if (companyId <= 0) {
            throw new IllegalArgumentException("companyId inválido.");
        }

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_NAME_SQL)
        ) {

            statement.setInt(1, companyId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return normalize(resultSet.getString("nome"));
                }

            }

        }

        return null;
    }



    private void bindCompany(
            PreparedStatement statement,
            String nome,
            String razaoSocial,
            String telefone,
            String email,
            String promptIa,
            String endereco,
            int dispositivosMax,
            int isActive
    ) throws SQLException {

        statement.setString(1, normalize(nome));
        statement.setString(2, normalize(razaoSocial));
        statement.setString(3, normalize(telefone));
        statement.setString(4, normalize(email));
        statement.setString(5, normalize(promptIa));
        statement.setString(6, normalize(endereco));
        statement.setInt(7, dispositivosMax);
        statement.setInt(8, isActive);

    }



    private void validateCompanyData(
            String nome,
            int dispositivosMax,
            int isActive
    ) {

        if (isBlank(nome)) {
            throw new IllegalArgumentException("Nome da empresa não pode ser vazio.");
        }

        if (dispositivosMax < 0) {
            throw new IllegalArgumentException("dispositivosMax inválido.");
        }

        if (isActive != 0 && isActive != 1) {
            throw new IllegalArgumentException("isActive deve ser 0 ou 1.");
        }

    }



    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}