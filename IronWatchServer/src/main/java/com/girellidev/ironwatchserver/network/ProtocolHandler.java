package com.girellidev.ironwatchserver.network;

import com.girellidev.ironwatchserver.model.User;
import com.girellidev.ironwatchserver.security.SecurityManager;
import com.girellidev.ironwatchserver.service.ChatService;

public class ProtocolHandler {

    private static final SecurityManager SECURITY_MANAGER = new SecurityManager();
    private static final ChatService CHAT_SERVICE = new ChatService();

    private ProtocolHandler() {
    }

    public static String handle(String rawRequest) {

        try {
            if (rawRequest == null || rawRequest.isBlank()) {
                return JsonUtil.toJson(RouteResponse.error("Requisicao vazia"));
            }

            RouteRequest request = JsonUtil.fromJson(rawRequest, RouteRequest.class);

            if (request == null || request.getAction() == null || request.getAction().isBlank()) {
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

        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.toJson(RouteResponse.error("Erro interno no protocolo"));
        }
    }

    private static RouteResponse handlePing() {
        return RouteResponse.ok("PONG");
    }

    private static RouteResponse handleAuthLogin(RouteRequest request) {
        if (isBlank(request.getLogin()) || isBlank(request.getPassword())) {
            return RouteResponse.error("Login e senha sao obrigatorios");
        }

        boolean valid = SECURITY_MANAGER.validateLogin(request.getLogin(), request.getPassword());

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