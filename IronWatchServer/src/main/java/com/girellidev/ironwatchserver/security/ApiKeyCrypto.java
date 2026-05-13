package com.girellidev.ironwatchserver.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.girellidev.ironwatchserver.logger.LoggerService;

public final class ApiKeyCrypto {
    private static final LoggerService logger = new LoggerService();

    private static final String SECRET =
            System.getenv().getOrDefault("IRONWATCH_AES_KEY",
                    "Colocar_a_merda_do_token_aqui_gordao");

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private ApiKeyCrypto() {}

    public static String encrypt(String plainText) {

        try {

            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key =
                    new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES");

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] encrypted =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];

            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            logger.erro("API_KEY", "Erro ao criptografar", e);
            throw new RuntimeException("Erro ao criptografar API key", e);
            

        }
    }

    public static String decrypt(String encryptedText) {

        try {

            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[decoded.length - IV_LENGTH];

            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key =
                    new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES");

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {

            throw new RuntimeException("Erro ao descriptografar API key", e);

        }
    }
}