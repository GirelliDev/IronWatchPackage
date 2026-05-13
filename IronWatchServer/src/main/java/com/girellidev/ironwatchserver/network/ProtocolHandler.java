package com.girellidev.ironwatchserver.network;

import java.util.Map;
import java.util.function.Supplier;

import com.girellidev.ironwatchserver.model.User;
import com.girellidev.ironwatchserver.security.AntiBruteForceService;
import com.girellidev.ironwatchserver.security.AuthCode;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.SecurityManager;
import com.girellidev.ironwatchserver.security.Session;
import com.girellidev.ironwatchserver.security.SessionManager;
import com.girellidev.ironwatchserver.security.loginAttempt;
import com.girellidev.ironwatchserver.service.ChatService;
import com.girellidev.ironwatchserver.service.CompanyService;

public class ProtocolHandler {
    private static final AntiBruteForceService ANTI_BRUTE_FORCE_SERVICE =
        new AntiBruteForceService();

    private static final SecurityManager SECURITY_MANAGER = new SecurityManager();
    private static final ChatService CHAT_SERVICE = new ChatService();
    private static final CompanyService COMPANY_SERVICE = new CompanyService();

    private ProtocolHandler() {
        
    }

    public static String handle(String request) {
        try {
            if (request == null || request.isBlank()) {
                return "ERROR|EMPTY_REQUEST";
            }

            request = request.trim();

            if (request.startsWith("{")) {
                return handleJsonRequest(request);
            }

            if (request.startsWith("AUTH|")) {
                return handleLegacyAuth(request);
            }

            if ("PING".equalsIgnoreCase(request)) {
                return "PONG";
            }

            return "UNKNOWN_COMMAND";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private static String handleLegacyAuth(String request) {
        String[] parts = request.split("\\|");

        if (parts.length < 2) {
            return "FAILED|INVALID_AUTH_FORMAT";
        }

        String code = parts[1];
        AuthCode authCode = CodeManager.validate(code);

        if (authCode == null) {
            return "FAILED";
        }

        return switch (authCode.getType()) {
            case MASTER_ADMIN -> {
                Session session = SessionManager.createSession("MASTER_DEVICE", "MASTER_ADMIN");
                yield "OK|" + session.getToken();
            }
            case ADMIN_INVITE -> {
                Session adminSession = SessionManager.createSession("ADMIN_DEVICE", "ADMIN");
                yield "OK|" + adminSession.getToken();
            }
            case EMPRESA_CLIENT -> {
                Session empresaSession = SessionManager.createSession("EMPRESA_DEVICE", "EMPRESA");
                yield "OK|" + empresaSession.getToken();
            }
            default -> "FAILED|INVALID_CODE_TYPE";
        };
    }

    private static String handleJsonRequest(String rawRequest) {
        RouteRequest request = JsonUtil.fromJson(rawRequest, RouteRequest.class);

        if (request == null || isBlank(request.getAction())) {
            return JsonUtil.toJson(RouteResponse.error("Action obrigatoria"));
        }

        RouteResponse response = switch (request.getAction().toUpperCase()) {
            case "PING" -> handlePing();
            case "AUTH_LOGIN" -> handleAuthLogin(request);
            case "SESSION_VALIDATE" -> handleSessionValidate(request);
            case "USER_CREATE" -> handleUserCreate(request);
            case "CHAT_SEND" -> handleChatSend(request);
            case "LIST_COMPANIES" -> handleListCompanies(request);
            case "COMPANY_CREATE" -> handleCompanyCreate(request);
            case "COMPANY_UPDATE" -> handleCompanyUpdate(request);
            case "COMPANY_DELETE" -> handleCompanyDelete(request);
            case "COMPANY_SET_ACTIVE" -> handleCompanySetActive(request);
            case "COMPANY_GENERATE_CODE" -> handleCompanyGenerateCode(request);
            default -> RouteResponse.error("Rota invalida: " + request.getAction());
        };

        return JsonUtil.toJson(response);
    }

    private static RouteResponse handlePing() {
        return RouteResponse.ok("PONG");
    }

    
    private static RouteResponse handleAuthLogin(RouteRequest request) {
    if (isBlank(request.getLogin()) || isBlank(request.getPassword())) {
        return RouteResponse.error("Login e senha sao obrigatorios");
    }

    String identificador = request.getLogin().trim().toLowerCase();

   loginAttempt tentativa = ANTI_BRUTE_FORCE_SERVICE.getAttempt(identificador);

    if (tentativa.estaBloqueado()) {
        return RouteResponse.error("Muitas tentativas de login. Tente novamente mais tarde.");
    }

    boolean valid = SECURITY_MANAGER.validateLogin(
            request.getLogin(),
            request.getPassword()
    );

    if (!valid) {
        ANTI_BRUTE_FORCE_SERVICE.registrarFalha(identificador);
        return RouteResponse.error("Login invalido");
    }

    ANTI_BRUTE_FORCE_SERVICE.registrarSucesso(identificador);

    String token = SECURITY_MANAGER.createSession(request.getLogin());

    if (token == null) {
        return RouteResponse.error("Nao foi possivel criar sessao");
    }

    User user = SECURITY_MANAGER.getUserByLogin(request.getLogin());

    if (user == null) {
        return RouteResponse.error("Usuario nao encontrado apos login");
    }

    return RouteResponse.okWithToken(
            "Login realizado com sucesso",
            token,
            user
    );
}

    private static RouteResponse handleSessionValidate(RouteRequest request) {
        return withValidSession(request, () -> RouteResponse.ok("Sessao valida"));
    }

    private static RouteResponse handleUserCreate(RouteRequest request) {
        if (isBlank(request.getLogin()) || isBlank(request.getPassword())) {
            return RouteResponse.error("Login e senha sao obrigatorios");
        }

        if (request.getRole() == null) {
            return RouteResponse.error("Role obrigatoria");
        }

        if (request.getEmpresaId() == null) {
            return RouteResponse.error("EmpresaId obrigatorio");
        }

        boolean created = SECURITY_MANAGER.createUser(
                request.getLogin(),
                request.getPassword(),
                request.getRole(),
                request.getEmpresaId()
        );

        if (!created) {
            return RouteResponse.error("Nao foi possivel criar usuario");
        }

        return RouteResponse.ok("Usuario criado com sucesso");
    }

    private static RouteResponse handleChatSend(RouteRequest request) {
        if (isBlank(request.getToken())) {
            return RouteResponse.error("Token obrigatorio");
        }

        if (isBlank(request.getMessage())) {
            return RouteResponse.error("Mensagem obrigatoria");
        }

        User user = SECURITY_MANAGER.getUserByToken(request.getToken());

        if (user == null) {
            return RouteResponse.error("Sessao invalida ou usuario nao encontrado");
        }

        String response = CHAT_SERVICE.processMessage(user, request.getMessage());

        return RouteResponse.ok("Mensagem processada com sucesso", response);
    }

    private static RouteResponse handleListCompanies(RouteRequest request) {
        return withValidSession(request, () ->
                executeSafely(() -> {
                    Map<String, Object> data = COMPANY_SERVICE.listCompaniesPayload();
                    return RouteResponse.ok("Empresas carregadas com sucesso", data);
                }, "Erro ao listar empresas: ")
        );
    }

    private static RouteResponse handleCompanyCreate(RouteRequest request) {
        if (isBlank(request.getNome())) {
            return RouteResponse.error("Nome da empresa obrigatorio");
        }

        if (isBlank(request.getRazaosocial())) {
            return RouteResponse.error("Razao social obrigatoria");
        }

        if (isBlank(request.getTelefone())) {
            return RouteResponse.error("Telefone obrigatorio");
        }

        if (isBlank(request.getEmail())) {
            return RouteResponse.error("Email obrigatorio");
        }

        if (isBlank(request.getEndereco())) {
            return RouteResponse.error("Endereco obrigatorio");
        }

        if (request.getDispositivosMax() == null || request.getDispositivosMax() <= 0) {
            return RouteResponse.error("dispositivos_max obrigatorio e deve ser maior que 0");
        }

        return withValidSession(request, () ->
                executeSafely(() -> {
                    int isActive = request.getIsActive() == null ? 1 : request.getIsActive();
                    int aiActive = request.getAiActive() == null ? 1 : request.getAiActive();

                    String aiProvider = isBlank(request.getAiProvider()) ? "openai" : request.getAiProvider();
                    String aiModel = isBlank(request.getAiModel()) ? "gpt-4o-mini" : request.getAiModel();
                    String promptIa = request.getPromptia();

                    boolean created = COMPANY_SERVICE.createCompany(
                            request.getNome(),
                            request.getRazaosocial(),
                            request.getTelefone(),
                            request.getEmail(),
                            promptIa,
                            request.getEndereco(),
                            request.getDispositivosMax(),
                            isActive,
                            aiProvider,
                            aiModel,
                            request.getAiApiKey(),
                            aiActive
                    );

                    if (!created) {
                        return RouteResponse.error("Nao foi possivel criar empresa");
                    }

                    return RouteResponse.ok("Empresa criada com sucesso");
                }, "Erro ao criar empresa: ")
        );
    }

    private static RouteResponse handleCompanyUpdate(RouteRequest request) {
        if (request.getCompanyId() == null) {
            return RouteResponse.error("company_id obrigatorio");
        }

        if (isBlank(request.getNome())) {
            return RouteResponse.error("Nome da empresa obrigatorio");
        }

        if (isBlank(request.getRazaosocial())) {
            return RouteResponse.error("Razao social obrigatoria");
        }

        if (isBlank(request.getTelefone())) {
            return RouteResponse.error("Telefone obrigatorio");
        }

        if (isBlank(request.getEmail())) {
            return RouteResponse.error("Email obrigatorio");
        }

        if (isBlank(request.getEndereco())) {
            return RouteResponse.error("Endereco obrigatorio");
        }

        if (request.getDispositivosMax() == null || request.getDispositivosMax() <= 0) {
            return RouteResponse.error("dispositivos_max obrigatorio e deve ser maior que 0");
        }

        if (request.getIsActive() == null) {
            return RouteResponse.error("is_active obrigatorio");
        }

        return withValidSession(request, () ->
                executeSafely(() -> {
                    boolean updated = COMPANY_SERVICE.updateCompany(
                            request.getCompanyId(),
                            request.getNome(),
                            request.getRazaosocial(),
                            request.getTelefone(),
                            request.getEmail(),
                            request.getPromptia(),
                            request.getEndereco(),
                            request.getDispositivosMax(),
                            request.getIsActive()
                    );

                    if (!updated) {
                        return RouteResponse.error("Nao foi possivel atualizar empresa");
                    }

                    return RouteResponse.ok("Empresa atualizada com sucesso");
                }, "Erro ao atualizar empresa: ")
        );
    }

    private static RouteResponse handleCompanyDelete(RouteRequest request) {
        if (request.getCompanyId() == null) {
            return RouteResponse.error("company_id obrigatorio");
        }

        return withValidSession(request, () ->
                executeSafely(() -> {
                    boolean deleted = COMPANY_SERVICE.deleteCompany(request.getCompanyId());

                    if (!deleted) {
                        return RouteResponse.error("Nao foi possivel apagar empresa");
                    }

                    return RouteResponse.ok("Empresa apagada com sucesso");
                }, "Erro ao apagar empresa: ")
        );
    }

    private static RouteResponse handleCompanySetActive(RouteRequest request) {
        if (request.getCompanyId() == null) {
            return RouteResponse.error("company_id obrigatorio");
        }

        if (request.getIsActive() == null) {
            return RouteResponse.error("is_active obrigatorio");
        }

        return withValidSession(request, () ->
                executeSafely(() -> {
                    boolean updated = COMPANY_SERVICE.setCompanyActive(
                            request.getCompanyId(),
                            request.getIsActive()
                    );

                    if (!updated) {
                        return RouteResponse.error("Nao foi possivel alterar status da empresa");
                    }

                    return RouteResponse.ok("Status da empresa atualizado com sucesso");
                }, "Erro ao alterar status da empresa: ")
        );
    }

    private static RouteResponse handleCompanyGenerateCode(RouteRequest request) {
        if (request.getCompanyId() == null) {
            return RouteResponse.error("company_id obrigatorio");
        }

        return withValidSession(request, () ->
                executeSafely(() -> {
                    Map<String, Object> data = COMPANY_SERVICE.generateCompanyCodePayload(
                            request.getCompanyId()
                    );

                    return RouteResponse.ok("Codigo gerado com sucesso", data);
                }, "Erro ao gerar codigo: ")
        );
    }

    private static RouteResponse withValidSession(RouteRequest request, Supplier<RouteResponse> action) {
        if (isBlank(request.getToken())) {
            return RouteResponse.error("Token obrigatorio");
        }

        boolean valid = SECURITY_MANAGER.validateSession(request.getToken());

        if (!valid) {
            return RouteResponse.error("Sessao invalida");
        }

        return action.get();
    }

    private static RouteResponse executeSafely(ThrowingSupplier<RouteResponse> action, String errorPrefix) {
        try {
            return action.get();
        } catch (Exception e) {
            e.printStackTrace();
            return RouteResponse.error(errorPrefix + e.getMessage());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}