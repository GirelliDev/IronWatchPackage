package com.girellidev.ironwatchserver.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                OutputStreamWriter writer = new OutputStreamWriter(
                        socket.getOutputStream()
                )
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("[TCP] RECEBIDO: " + line);

                String response = ProtocolHandler.handle(line);

                System.out.println("[TCP] RESPOSTA: " + response);

                writer.write(response + "\n");
                writer.flush();
            }

        } catch (Exception e) {
            System.out.println("[TCP] Cliente desconectado: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}