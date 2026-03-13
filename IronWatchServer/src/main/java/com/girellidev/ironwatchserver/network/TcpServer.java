package com.girellidev.ironwatchserver.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    private int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() {

        try {

            ServerSocket server = new ServerSocket(port);

            System.out.println("[TCP] Servidor Iniciado e Ouvindo em: " + port);

            while (true) {

                Socket socket = server.accept();

                System.out.println("[TCP] Cliente conectado: " + socket.getInetAddress());

                new Thread(() -> handle(socket)).start();

            }

        } catch (Exception e) {

            System.out.println("Erro no servidor TCP");
            e.printStackTrace();

        }

    }

    private void handle(Socket socket) {

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter writer = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            String request;

            while ((request = reader.readLine()) != null) {

                System.out.println("[TCP] RECEBIDO: " + request);

                String response = ProtocolHandler.handle(request);

                System.out.println("[TCP] RESPOSTA: " + response);

                writer.println(response);

            }

        } catch (Exception e) {

            System.out.println("[TCP] Cliente desconectado");

        } finally {

            try {
                socket.close();
            } catch (Exception ignored) {
            }

        }

    }

}