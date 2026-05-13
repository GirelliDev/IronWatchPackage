package com.girellidev.ironwatchserver.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {

    private static final String ERRO = "[ERRO]";
    private static final String AVISO = "[WARN]";
    private static final String LOG = "[LOG]";
    private static final String DEBUG = "[DEBUG]";
    private static final String INFO = "[INFO]";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Path LOG_DIR =
            Path.of("logs");

    public LoggerService() {
        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            System.err.println("Falha ao criar pasta de logs");
            e.printStackTrace();
        }
    }

    private String horario() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private Path arquivoAtual() {
        String nomeArquivo =
                LocalDate.now() + ".log";

        return LOG_DIR.resolve(nomeArquivo);
    }

    private synchronized void print(
            String tipo,
            String origem,
            String mensagem
    ) {
        String linha =
                "[" + horario() + "] "
                        + tipo
                        + " ["
                        + origem
                        + "] "
                        + mensagem;

        System.out.println(linha);

        try {
            Files.writeString(
                    arquivoAtual(),
                    linha + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Falha ao salvar log");
            e.printStackTrace();
        }
    }

    public void log(String origem, String mensagem) {
        print(LOG, origem, mensagem);
    }

    public void info(String origem, String mensagem) {
        print(INFO, origem, mensagem);
    }

    public void aviso(String origem, String mensagem) {
        print(AVISO, origem, mensagem);
    }

    public void erro(String origem, String mensagem) {
        print(ERRO, origem, mensagem);
    }

    public void erro(
            String origem,
            String mensagem,
            Exception exception
    ) {
        print(ERRO, origem, mensagem);

        if (exception != null) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            exception.printStackTrace(pw);

            print(
                    ERRO,
                    origem,
                    sw.toString()
            );
        }
    }

    public void debug(String origem, String mensagem) {
        print(DEBUG, origem, mensagem);
    }
}