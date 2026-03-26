package com.girellidev.ironwatchserver.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.girellidev.ironwatchserver.dao.CompanyAIConfigDAO;
import com.girellidev.ironwatchserver.dao.CompanyDAO;
import com.girellidev.ironwatchserver.dto.CompanyStatusDTO;
import com.girellidev.ironwatchserver.security.ApiKeyCrypto;

public class CompanyService {

    private final CompanyDAO companyDAO = new CompanyDAO();
    private final CompanyAIConfigDAO companyAIConfigDAO = new CompanyAIConfigDAO();

    public Map<String, Object> listCompaniesPayload() throws Exception {
        List<CompanyStatusDTO> companies = companyDAO.listAllCompanies();

        Map<String, Object> data = new HashMap<>();
        data.put("companies", companies);

        return data;
    }

    public boolean createCompany(
            String nome,
            String razaoSocial,
            String telefone,
            String email,
            String promptIa,
            String endereco,
            int dispositivosMax,
            int isActive,
            String aiProvider,
            String aiModel,
            String aiApiKey,
            int aiActive
    ) throws Exception {

        if (!isValidCompanyData(
                nome,
                razaoSocial,
                telefone,
                email,
                endereco,
                dispositivosMax,
                isActive
        )) {
            return false;
        }

        if (aiActive != 0 && aiActive != 1) {
            return false;
        }

        if (aiProvider == null || aiProvider.isBlank()) {
            aiProvider = "openai";
        }

        if (aiModel == null || aiModel.isBlank()) {
            aiModel = "gpt-4o-mini";
        }

        int companyId = companyDAO.createCompany(
                nome.trim(),
                razaoSocial.trim(),
                telefone.trim(),
                email.trim(),
                normalizeNullable(promptIa),
                endereco.trim(),
                dispositivosMax,
                isActive
        );

        if (companyId <= 0) {
            return false;
        }

        if (aiApiKey != null && !aiApiKey.isBlank()) {
            String encryptedKey = ApiKeyCrypto.encrypt(aiApiKey.trim());

            return companyAIConfigDAO.createAIConfig(
                    companyId,
                    aiProvider.trim(),
                    encryptedKey,
                    aiModel.trim(),
                    aiActive
            );
        }

        return true;
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
            Integer isActive
    ) throws Exception {

        if (companyId <= 0) {
            return false;
        }

        if (isActive == null) {
            return false;
        }

        if (!isValidCompanyData(
                nome,
                razaoSocial,
                telefone,
                email,
                endereco,
                dispositivosMax,
                isActive
        )) {
            return false;
        }

        return companyDAO.updateCompany(
                companyId,
                nome.trim(),
                razaoSocial.trim(),
                telefone.trim(),
                email.trim(),
                normalizeNullable(promptIa),
                endereco.trim(),
                dispositivosMax,
                isActive
        );
    }

    public boolean deleteCompany(int companyId) throws Exception {
        if (companyId <= 0) {
            return false;
        }

        return companyDAO.deleteCompany(companyId);
    }

    public boolean setCompanyActive(int companyId, int isActive) throws Exception {
        if (companyId <= 0) {
            return false;
        }

        if (isActive != 0 && isActive != 1) {
            return false;
        }

        return companyDAO.setCompanyActive(companyId, isActive);
    }

    public Map<String, Object> generateCompanyCodePayload(int companyId) throws Exception {
        if (companyId <= 0) {
            throw new IllegalArgumentException("companyId invalido");
        }

        String companyName = companyDAO.getCompanyNameById(companyId);

        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Empresa nao encontrada");
        }

        String code = generateSimpleCode(8);
        String expiresAt = "7 dias";

        Map<String, Object> data = new HashMap<>();
        data.put("company_id", companyId);
        data.put("company_name", companyName);
        data.put("code", code);
        data.put("expires_at", expiresAt);

        return data;
    }

    private boolean isValidCompanyData(
            String nome,
            String razaoSocial,
            String telefone,
            String email,
            String endereco,
            int dispositivosMax,
            int isActive
    ) {
        if (nome == null || nome.isBlank()) {
            return false;
        }

        if (razaoSocial == null || razaoSocial.isBlank()) {
            return false;
        }

        if (telefone == null || telefone.isBlank()) {
            return false;
        }

        if (email == null || email.isBlank()) {
            return false;
        }

        if (endereco == null || endereco.isBlank()) {
            return false;
        }

        if (dispositivosMax <= 0) {
            return false;
        }

        return isActive == 0 || isActive == 1;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateSimpleCode(int size) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}