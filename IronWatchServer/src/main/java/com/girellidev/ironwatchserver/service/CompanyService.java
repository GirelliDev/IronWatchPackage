package com.girellidev.ironwatchserver.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.girellidev.ironwatchserver.dao.CompanyDAO;
import com.girellidev.ironwatchserver.dto.CompanyStatusDTO;

public class CompanyService {

    private final CompanyDAO companyDAO = new CompanyDAO();

    public Map<String, Object> listCompaniesPayload() throws Exception {
        List<CompanyStatusDTO> companies = companyDAO.listAllCompanies();

        Map<String, Object> data = new HashMap<>();
        data.put("companies", companies);

        return data;
    }

    public boolean createCompany(String nome, int isActive) throws Exception {
        if (nome == null || nome.isBlank()) {
            return false;
        }

        if (isActive != 0 && isActive != 1) {
            return false;
        }

        return companyDAO.createCompany(nome.trim(), isActive);
    }

    public boolean updateCompany(int companyId, String nome, Integer isActive) throws Exception {
        if (companyId <= 0) {
            return false;
        }

        if (nome == null || nome.isBlank()) {
            return false;
        }

        if (isActive == null || (isActive != 0 && isActive != 1)) {
            return false;
        }

        return companyDAO.updateCompany(companyId, nome.trim(), isActive);
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