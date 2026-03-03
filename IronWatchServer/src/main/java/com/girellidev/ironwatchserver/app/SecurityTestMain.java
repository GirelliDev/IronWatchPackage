package com.girellidev.ironwatchserver.app;

import com.girellidev.ironwatchserver.security.PasswordHasher;
import com.girellidev.ironwatchserver.security.TokenGenerator;
import com.girellidev.ironwatchserver.security.SessionManager;

public class SecurityTestMain {

    public static void main(String[] args) {

        System.out.println("Gerando hash...");
        String hash = PasswordHasher.hash("123456");
        System.out.println("Hash: " + hash);

        System.out.println("Validando senha...");
        System.out.println("Senha correta? " + PasswordHasher.verify("123456", hash));

        System.out.println("Código admin: " + TokenGenerator.generateAlphaNumericCode(8));
        System.out.println("Código cliente: " + TokenGenerator.generateNumericCode(6));

        System.out.println("Session: " + TokenGenerator.generateSessionToken());

        System.out.println("Expira em: " + SessionManager.expirationMinutes(10));
    }
}