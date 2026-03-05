package com.girellidev.ironwatchserver;

import com.girellidev.ironwatchserver.network.TcpServer;
import com.girellidev.ironwatchserver.security.CodeManager;
import com.girellidev.ironwatchserver.security.CodeType;

public class IronWatchServer {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("CORELABS - IRONWATCH V3");
        System.out.println("=================================");
        System.out.println("[BOOT] Tentando iniciar...");
        System.out.println("[BOOT] INICIADO");
        System.out.println("[BOOT] Gerando Codigo Master Admin....");
        try {

            // gera código MASTER para login inicial
            var masterCode = CodeManager.generate(CodeType.MASTER_ADMIN);

            System.out.println("[BOOT] CODIGO MASTER ADMIN: " + masterCode.getCode());

            // porta do servidor
            int port = 5555;

            TcpServer server = new TcpServer(port);

            System.out.println("[BOOT] Tentando Iniciar Protocolo TCP....");

            server.start();

        } catch (Exception e) {

            System.out.println("Servidor não conseguiu iniciar, Verifique erros abaixo, mocorongo");
            e.printStackTrace();

        }

    }
}