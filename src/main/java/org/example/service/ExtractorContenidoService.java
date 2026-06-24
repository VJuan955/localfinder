package org.example.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de extraer contenido textual y calcular hashes
 * de documentos soportados por el sistema.
 *
 * <p>Utiliza Apache Tika para procesar diferentes formatos de archivo
 * y generar una representación textual unificada que posteriormente
 * puede ser indexada por el motor de búsqueda.</p>
 *
 * <p>También proporciona funcionalidades para calcular hashes SHA-256
 * utilizados en la detección de modificaciones y duplicados.</p>
 *
 * @author FiscalPro
 * @version 1.0
 */
public class ExtractorContenidoService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractorContenidoService.class);

    /**
     * Instancia de Apache Tika utilizada para la extracción de contenido.
     */
    private final Tika tika;

    /**
     * Inicializa el servicio de extracción de contenido.
     *
     * <p>Configura Apache Tika para permitir la extracción completa del
     * contenido textual sin restricciones de longitud.</p>
     */
    public ExtractorContenidoService() {
        this.tika = new Tika();

        this.tika.setMaxStringLength(10_000_000);
    }

    /**
     * Extrae el contenido textual de un archivo utilizando Apache Tika.
     *
     * @param rutaArchivo ruta del archivo a procesar
     * @return texto extraído o una cadena vacía si ocurre un error
     */
    public String extraerTexto(Path rutaArchivo) {
        try {
            String extraccion = tika.parseToString(rutaArchivo);
            logger.debug("Contenido extraído correctamente: {}", rutaArchivo);
            return extraccion != null ? extraccion : "";
        } catch (IOException | TikaException e) {
            logger.warn("No fue posible extraer contenido de {}", rutaArchivo, e);
            return "";
        }
    }

    /**
     * Calcula el hash SHA-256 de un archivo.
     *
     * @param rutaArchivo ruta del archivo
     * @return representación hexadecimal del hash calculado o
     *         {@code null} si ocurre un error
     */
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

                logger.trace("Hash SHA-256 calculado para {}", rutaArchivo);
                return hexString.toString();
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn("No fue posible calcular el hash de {}", rutaArchivo, e);
            return "";
        }
    }
}
