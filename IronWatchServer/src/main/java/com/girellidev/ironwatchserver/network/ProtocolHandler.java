package com.girellidev.ironwatchserver.network;

import com.girellidev.ironwatchserver.model.User;
import com.girellidev.ironwatchserver.security.AuthCode;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.SecurityManager;
import com.girellidev.ironwatchserver.security.Session;
import com.girellidev.ironwatchserver.security.SessionManager;
import com.girellidev.ironwatchserver.service.ChatService;

public class ProtocolHandler {

    private static final SecurityManager SECURITY_MANAGER = new SecurityManager();
    private static final ChatService CHAT_SERVICE = new ChatService();

    private ProtocolHandler() {
    }

    public static String handle(String request) {
        try {
            if (request == null || request.isBlank()) {
                return "ERROR|EMPTY_REQUEST";
            }

            request = request.trim();

            // JSON novo
            if (request.startsWith("{")) {
                return handleJsonRequest(request);
            }

            // Legado
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

        switch (authCode.getType()) {
            case MASTER_ADMIN -> {
                Session session = SessionManager.createSession("MASTER_DEVICE", "MASTER_ADMIN");
                return "OK|" + session.getToken();
            }

            case ADMIN_INVITE -> {
                Session adminSession = SessionManager.createSession("ADMIN_DEVICE", "ADMIN");
                return "OK|" + adminSession.getToken();
            }

            case EMPRESA_CLIENT -> {
                Session empresaSession = SessionManager.createSession("EMPRESA_DEVICE", "EMPRESA");
                return "OK|" + empresaSession.getToken();
            }

            default -> {
                return "FAILED|INVALID_CODE_TYPE";
            }
        }
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

        boolean valid = SECURITY_MANAGER.validateLogin(
                request.getLogin(),
                request.getPassword()
        );

        if (!valid) {
            return RouteResponse.error("Login invalido");
        }

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
        if (isBlank(request.getToken())) {
            return RouteResponse.error("Token obrigatorio");
        }

        boolean valid = SECURITY_MANAGER.validateSession(request.getToken());

        if (!valid) {
            return RouteResponse.error("Sessao invalida");
        }

        return RouteResponse.ok("Sessao valida");
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}