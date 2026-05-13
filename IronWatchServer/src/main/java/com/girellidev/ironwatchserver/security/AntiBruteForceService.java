package com.girellidev.ironwatchserver.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AntiBruteForceService {

    public static final int TENTATIVAS_MAXIMAS = 5;

    private static final long TEMPO_BLOQUEIO =
            15 * 60 * 1000;

    private final Map<String, loginAttempt> tentativas =
            new ConcurrentHashMap<>();

    public boolean podeTentarLogin(String identificador) {
        loginAttempt tentativa = tentativas.get(identificador);

        if (tentativa == null) {
            return true;
        }

        return !tentativa.estaBloqueado();
    }

    public void registrarFalha(String identificador) {
        loginAttempt tentativa = tentativas.computeIfAbsent(
                identificador,
                key -> new loginAttempt()
        );

        tentativa.incrementar();

        if (tentativa.getTentativas() >= TENTATIVAS_MAXIMAS) {
            tentativa.bloquearPor(TEMPO_BLOQUEIO);
        }
    }

    public void registrarSucesso(String identificador) {
        loginAttempt tentativa = tentativas.get(identificador);

        if (tentativa != null) {
            tentativa.resetar();
        }
    }

    public int getTentativas(String identificador) {
        loginAttempt tentativa = tentativas.get(identificador);

        if (tentativa == null) {
            return 0;
        }

        return tentativa.getTentativas();
    }

    public loginAttempt getAttempt(String identificador) {
        return tentativas.computeIfAbsent(
                identificador,
                key -> new loginAttempt()
        );
    }
}