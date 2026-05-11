package com.girellidev.ironwatchserver.security;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public final class SSLServerSocketProvider {

    private static final String KEYSTORE_TYPE = "PKCS12";

    private static final String KEYSTORE_PATH =
            "security/server.p12";

    private static final String KEYSTORE_PASSWORD =
            "Colocar_a_merda_da_senha_aqui_gordao";

    private static final String TRUSTSTORE_PATH =
            "security/truststore.p12";

    private static final String TRUSTSTORE_PASSWORD =
            "Colocar_a_merda_da_senha_aqui_gordao";

    private SSLServerSocketProvider() {
    }

    public static SSLServerSocket criar(int porta) {

        try {

            /*
             * KEYSTORE
             */

            KeyStore keyStore =
                    KeyStore.getInstance(KEYSTORE_TYPE);

            try (FileInputStream fis =
                         new FileInputStream(KEYSTORE_PATH)) {

                keyStore.load(
                        fis,
                        KEYSTORE_PASSWORD.toCharArray()
                );
            }

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(
                            KeyManagerFactory.getDefaultAlgorithm()
                    );

            keyManagerFactory.init(
                    keyStore,
                    KEYSTORE_PASSWORD.toCharArray()
            );

            /*
             * TRUSTSTORE
             */

            KeyStore trustStore =
                    KeyStore.getInstance(KEYSTORE_TYPE);

            try (FileInputStream fis =
                         new FileInputStream(TRUSTSTORE_PATH)) {

                trustStore.load(
                        fis,
                        TRUSTSTORE_PASSWORD.toCharArray()
                );
            }

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(
                            TrustManagerFactory.getDefaultAlgorithm()
                    );

            trustManagerFactory.init(trustStore);

            /*
             * SSL CONTEXT
             */

            SSLContext sslContext =
                    SSLContext.getInstance("TLS");

            sslContext.init(
                    keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(),
                    null
            );

            SSLServerSocketFactory factory =
                    sslContext.getServerSocketFactory();

            SSLServerSocket serverSocket =
                    (SSLServerSocket)
                            factory.createServerSocket(porta);

            serverSocket.setEnabledProtocols(
                    new String[]{
                            "TLSv1.2",
                            "TLSv1.3"
                    }
            );

            /*
             * Exige certificado do cliente
             */

            serverSocket.setNeedClientAuth(true);

            return serverSocket;

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Erro ao iniciar SSL",
                    e
            );
        }
    }
}