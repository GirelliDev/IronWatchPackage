package com.girellidev.ironwatchserver.security;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

public final class SSLServerSocketProvider {

    private SSLServerSocketProvider() {
    }

    public static SSLServerSocket criar(
            int porta,
            String keystorePath,
            String keystorePassword
    ) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keyStore.load(fis, keystorePassword.toCharArray());
            }

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    null,
                    null
            );

            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();

            SSLServerSocket serverSocket =
                    (SSLServerSocket) factory.createServerSocket(porta);

            serverSocket.setEnabledProtocols(new String[]{
                    "TLSv1.2",
                    "TLSv1.3"
            });

            return serverSocket;

        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criar SSLServerSocket", e);
        }
    }
}