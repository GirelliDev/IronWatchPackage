package com.girellidev.ironwatchserver.service;

import com.girellidev.ironwatchserver.dao.EmpresaAIConfigDAO;
import com.girellidev.ironwatchserver.model.EmpresaAIConfig;
import com.girellidev.ironwatchserver.security.ApiKeyCrypto;

public class OpenAIService {

    private final EmpresaAIConfigDAO empresaAIConfigDAO;

    public OpenAIService() {
        this.empresaAIConfigDAO = new EmpresaAIConfigDAO();
    }

    public String generateResponse(int empresaId, String userMessage) {
        try {
            EmpresaAIConfig config = empresaAIConfigDAO.findActiveByEmpresaId(empresaId);

            if (config == null) {
                return "Nenhuma configuracao de IA ativa encontrada para esta empresa.";
            }

            String apiKey = ApiKeyCrypto.decrypt(config.getApiKeyEncrypted());
            String model = config.getModel();

            // Aqui entra a chamada real pra OpenAI
            // Por enquanto, mock simples:
            return "[IA/" + model + "] Resposta para: " + userMessage;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao processar mensagem com a IA.";
        }
    }
}