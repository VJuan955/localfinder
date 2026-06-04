package org.example.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExtractorContenidoService {
    private final Tika tika;

    public ExtractorContenidoService() {
        this.tika = new Tika();

        this.tika.setMaxStringLength(-1);
    }

    public String extraerTexto(Path rutaArchivo) {
        try {
            return tika.parseToString(rutaArchivo);
        } catch (IOException | TikaException e) {
            System.err.println("Advertencia - No se pudo procesar el contenido de: "
                    + rutaArchivo.toString() + " | Motivo: " + e.getMessage());
            return "";
        }
    }

    public String calcularHash(Path rutaArchivo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream is = Files.newInputStream(rutaArchivo)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }

                byte[] hashBytes = digest.digest();

                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("Advertencia - No se pudo calcular el hash de: "
                    + rutaArchivo.toString() + " | Motivo: " + e.getMessage());
            return null;
        }
    }
}
