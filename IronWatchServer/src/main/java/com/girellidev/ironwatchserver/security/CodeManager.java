package com.girellidev.ironwatchserver.security;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CodeManager {

    private static final Map<String, AuthCode> codes = new ConcurrentHashMap<>();

    public static AuthCode generate(CodeType type) {

        String code;

        switch (type) {
            case MASTER_ADMIN -> code = TokenGenerator.generateAlphaNumericCode(8);
            case ADMIN_INVITE -> code = TokenGenerator.generateNumericCode(6);
            case EMPRESA_CLIENT -> code = TokenGenerator.generateNumericCode(6);
            default -> throw new IllegalArgumentException("Tipo inválido");
        }

        // 5 min de validade (ajusta se quiser)
        AuthCode auth = new AuthCode(code, type, LocalDateTime.now().plusMinutes(5));

        codes.put(code, auth);

        return auth;
    }

    public static AuthCode validate(String code) {

        AuthCode auth = codes.get(code);

        if (auth == null) return null;

        if (auth.isExpired()) {
            codes.remove(code);
            return null;
        }

        // código de uso único
        codes.remove(code);

        return auth;
    }
}