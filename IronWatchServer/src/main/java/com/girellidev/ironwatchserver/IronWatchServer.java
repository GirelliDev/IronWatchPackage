package com.girellidev.ironwatchserver;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import com.girellidev.ironwatchserver.logger.LoggerService;
import com.girellidev.ironwatchserver.network.TcpServer;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.CodeType;
import com.girellidev.ironwatchserver.security.ConsoleQrRenderer;
import com.girellidev.ironwatchserver.security.LoginQrPayload;

public class IronWatchServer {
   private static final LoggerService logger = new LoggerService();


    private static final String IMAGE_PATH = resolveImagePath();
    
    private static String resolveImagePath() {
        // Tenta primeiro o caminho relativo (execução a partir de IronWatchServer)
        File relativePath = new File("src/main/java/com/girellidev/ironwatchserver/assets/1.jpg");
        if (relativePath.exists()) {
            return relativePath.getAbsolutePath();
        }
        
        // Tenta a partir de um nível acima (execução a partir de IronWatchPackage)
        File parentPath = new File("IronWatchServer/src/main/java/com/girellidev/ironwatchserver/assets/1.jpg");
        if (parentPath.exists()) {
            return parentPath.getAbsolutePath();
        }
        
        // Retorna o caminho padrão se nenhum for encontrado
        return "src/main/java/com/girellidev/ironwatchserver/assets/1.jpg";
    }

    private static final String EXPECTED_HASH =
            "fd2d474da42a501bf4fb31fdde62dab2da615db8f0dd7cc74f460997ebf56dfd";

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("CORELABS - IRONWATCH V3");
        System.out.println("=================================");
        logger.info("BOOT","=================================");
        logger.info("BOOT","CORELABS - IRONWATCH V3");
        logger.info("BOOT","=================================");

        logger.info("BOOT","Verificando Artefado Obricatório");

        if (!validateImage()) {
            logger.erro("BOOT","Erro Critico, Arquivo obrigatório ausente ou alterado. Caminho esperado:"+ IMAGE_PATH);
            return;
        }
        logger.info("BOOT","Artefado Validado com Sucesso");
        logger.info("BOOT","Iniciando...");

        try {
            var masterCode = CodeManager.generate(CodeType.MASTER_ADMIN);


            LoginQrPayload qrPayload = new LoginQrPayload(
                    masterCode.getCode()
            );

            String qrJson = qrPayload.toJson();

            System.out.println("[BOOT] CODIGO MASTER ADMIN: " + masterCode.getCode());
            System.out.println("[BOOT] USE SUAS CREDENCIAIS PARA LOGAR");

            ConsoleQrRenderer.printQr(qrJson);

            int port = Integer.parseInt(System.getenv("SERVER_PORT") != null ? System.getenv("SERVER_PORT") : "5555");
            TcpServer server = new TcpServer(port);

           logger.info("BOOT","Iniciando Servidor TCP");

            server.start();

        } catch (Exception e) {
         logger.erro(
             "BOOT",
              "Erro critico ao iniciar servidor",
               e
         );
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