package com.girellidev.ironwatchserver.security;

import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ConsoleQrRenderer {

    private static final String BLACK = "██";
    private static final String WHITE = "  ";

    private ConsoleQrRenderer() {
    }

    public static void printQr(String content) {
        try {
            BitMatrix matrix = generateMatrix(content);
            printMatrix(matrix);
        } catch (WriterException e) {
            System.out.println("[BOOT] Falha ao gerar QR no terminal");
            e.printStackTrace();
        }
    }

    private static BitMatrix generateMatrix(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        return writer.encode(content, BarcodeFormat.QR_CODE, 1, 1, hints);
    }

    private static void printMatrix(BitMatrix matrix) {
        System.out.println();
        System.out.println("[BOOT] QR LOGIN:");
        System.out.println();

        for (int y = 0; y < matrix.getHeight(); y++) {
            StringBuilder line = new StringBuilder();

            for (int x = 0; x < matrix.getWidth(); x++) {
                line.append(matrix.get(x, y) ? BLACK : WHITE);
            }

            System.out.println(line);
        }

        System.out.println();
    }
}