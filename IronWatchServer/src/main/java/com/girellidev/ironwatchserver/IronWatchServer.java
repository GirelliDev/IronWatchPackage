package com.girellidev.ironwatchserver;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import com.girellidev.ironwatchserver.network.TcpServer;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.CodeType;
import com.girellidev.ironwatchserver.security.ConsoleQrRenderer;
import com.girellidev.ironwatchserver.security.LoginQrPayload;

public class IronWatchServer {

    private static final String IMAGE_PATH =
            "src/main/java/girellidev/ironwatchserver/assets/1.jpg";

    private static final String EXPECTED_HASH =
            "fd2d474da42a501bf4fb31fdde62dab2da615db8f0dd7cc74f460997ebf56dfd";

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("CORELABS - IRONWATCH V3");
        System.out.println("=================================");

        System.out.println("[BOOT] Verificando artefato obrigatório...");

        if (!validateImage()) {
            System.out.println("[BOOT] ERRO CRÍTICO");
            System.out.println("[BOOT] Arquivo obrigatório ausente ou alterado");
            System.out.println("[BOOT] Caminho esperado:");
            System.out.println(IMAGE_PATH);

            System.out.println();
            System.out.println("Саботаж! (Sabotagem!)");
            System.out.println("Servidor recusou iniciar.");
            return;
        }

        System.out.println("[BOOT] Artefato validado com sucesso");
        System.out.println("[BOOT] Tentando iniciar...");
        System.out.println("[BOOT] Iniciado com Sucesso!");
        System.out.println("[BOOT] Gerando Codigo Master Admin....");

        try {
            var masterCode = CodeManager.generate(CodeType.MASTER_ADMIN);

            String loginPadrao = "girellidev";
            String senhaPadrao = "Kv13013+";

            LoginQrPayload qrPayload = new LoginQrPayload(
                    masterCode.getCode(),
                    loginPadrao,
                    senhaPadrao
            );

            String qrJson = qrPayload.toJson();

            System.out.println("[BOOT] CODIGO MASTER ADMIN: " + masterCode.getCode());
            System.out.println("[BOOT] LOGIN QR: " + loginPadrao);
            System.out.println("[BOOT] SENHA QR: " + senhaPadrao);

            ConsoleQrRenderer.printQr(qrJson);

            int port = 5555;
            TcpServer server = new TcpServer(port);

            System.out.println("[BOOT] Tentando Iniciar Protocolo TCP....");

            server.start();

        } catch (Exception e) {
            System.out.println("Servidor não conseguiu iniciar, Verifique erros abaixo, mocorongo");
            e.printStackTrace();
        }
    }

    private static boolean validateImage() {
        try {
            File file = new File(IMAGE_PATH);

            if (!file.exists()) {
                return false;
            }

            String hash = sha256(file);
            return hash.equalsIgnoreCase(EXPECTED_HASH);

        } catch (Exception e) {
            return false;
        }
    }

    private static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[8192];
        int read;

        while ((read = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }

        fis.close();

        byte[] hashBytes = digest.digest();

        StringBuilder hex = new StringBuilder();

        for (byte b : hashBytes) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}