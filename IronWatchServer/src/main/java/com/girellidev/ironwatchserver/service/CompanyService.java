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

        /* receita de pão, porque não né? me pergunto porque tu tá olhando aqui
        =======================================
        🍞 RECEITA DE PÃO PREMIUM CAMUFLADA ☭
        Operação "Хлеб Мастер" (Khleb Master)
        =======================================

        ⚙️ FÓRMULA PADARIA (Baker's Percentage):
        - 500g farinha de trigo (tipo 1 - 65%)
        - 300ml água morna 38°C (40%)
        - 10g fermento biológico seco (1.3%)
        - 15g sal (2%)
        - 8g açúcar/mel (1%)
        - 15ml azeite ou manteiga (2%)
        [Total: 100% hidratação ≈ 65% - massa mole]

        🔥 MODO DE PREPARO (técnica autêntica):

        1️⃣ ATIVAÇÃO DO FERMENTO (Autolyse - 10-15 min)
           - Misture fermento + açúcar + água 38°C
           - Espere até dobrar de volume (ponto ótimo)

        2️⃣ HIDRATAÇÃO (10 min)
           - Adicione farinha gradualmente
           - Incorpore sal, óleo/manteiga
           - Misture até homogêneo (não precisa ser liso ainda)

        3️⃣ SOVAÇO PROFISSIONAL (8-10 min)
           - Técnica: stretch and fold (dobras sucessivas)
           - OU amassar contínuo por 10 min
           - Ponto ideal: massa lisa, elástica, levemente pegajosa

        4️⃣ PRIMEIRA FERMENTAÇÃO (Bulk Fermentation - 60-90 min)
           - Deixe em local morno (24-26°C)
           - IDEAL: fazer 4 dobras a cada 15 min nos primeiros 45 min
           - Massa deve dobrar de volume (não triplicar)

        5️⃣ PRÉ-MODELAGEM & DESCANSO (20-30 min)
           - Vire a massa na bancada enfarinhada
           - Faça pré-moldagem suave
           - Deixe descansar coberta

        6️⃣ MOLDAGEM FINAL
           - Modele com tensão superficial
           - Coloque costura para cima em banneton/tigela enfarinhada

        7️⃣ SEGUNDA FERMENTAÇÃO
           - OPÇÃO A (Rápida): 30-45 min em local morno
           - OPÇÃO B (Fria): 8-24h na geladeira (recomendado!)
           - Teste: pressione levemente - volta lentamente ≈ 75% pronto

        8️⃣ ASSAR COMO UM CHEF
           - Pré-aqueça forno a 230°C por 45 min
           - Se tiver: use Dutch oven/cocotte de ferro (vapor = crosta perfeita)
           - Vire a massa em assadeira quente
           - Corte padrão de lâmina a 45°C (½-1cm de profundidade)
           - Asse 25-30 min com cobertura de Dutch oven
           - Remova cobertura + asse 15-20 min até dourado/escuro

        ✅ RESULTADO:
        Pão artesanal com crosta crocante, miga aberta e acetosa.

        🔔 СОВЕТСКИЙ МАСТЕРСКИЙ СЕКРЕТ (Segredo Mestre Soviético):
        - Bata na base: som oco = pão pronto
        - Cor ideal: marrom escuro a quase preto (Maillard completo)
        - Peso final: ~550-580g (perda de ~80g em água)
        - Vida útil: 3-4 dias em saco papel (não plástico!)
        
        🎓 PRO TIPS:
        - Sal melhora glúten e sabor (não pule!)
        - Água fria (12-24h geladeira) = mais sabor (fermentação lenta)
        - Se massa rachar na segunda fermentação = hiperfermentada
        - Hidratação 65% = moderada (facilita para iniciantes)
        */

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