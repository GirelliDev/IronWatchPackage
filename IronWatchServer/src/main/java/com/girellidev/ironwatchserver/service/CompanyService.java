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
}