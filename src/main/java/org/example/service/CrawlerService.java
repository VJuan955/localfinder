package org.example.service;

import org.example.model.Archivo;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de recorrer directorios del sistema de archivos
 * y detectar documentos compatibles para su posterior indexación.
 *
 * <p>Actualmente admite archivos PDF, TXT y DOCX. Durante el proceso
 * se recopila información básica de cada archivo encontrada, como
 * ruta, tamaño, tipo y fecha de modificación.</p>
 *
 * <p>El recorrido se realiza de forma recursiva utilizando la API
 * {@link java.nio.file.Files#walkFileTree(Path, FileVisitor)}.</p>
 *
 * @author FiscalPro
 * @version 1.0
 */
public class CrawlerService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerService.class);

    /**
     * Conjunto de extensiones de archivo admitidas por el sistema.
     */
    private static final Set<String> FORMATOS_PERMITIDOS = Set.of("pdf", "txt", "docx");

    /**
     * Explora recursivamente un directorio y recupera los archivos
     * compatibles con el sistema de indexación.
     *
     * @param rutaDirectorio ruta absoluta o relativa del directorio
     *                       que será explorado
     * @return lista de archivos detectados
     */
    public List<Archivo> rastrearDirectorio(String rutaDirectorio) {
        logger.info("Iniciando rastreo del directorio: {}", rutaDirectorio);

        List<Archivo> archivosDetectados = new ArrayList<>();
        Path directorioRaiz = Paths.get(rutaDirectorio);

        if (!Files.exists(directorioRaiz) || !Files.isReadable(directorioRaiz)) {
            logger.warn("Directorio inválido o acceso denegado: {}", rutaDirectorio);
            return archivosDetectados;
        }

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        try {
            Files.walkFileTree(directorioRaiz, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String nombre = file.getFileName().toString();
                    String extension = obtenerExtension(nombre);

                    if (FORMATOS_PERMITIDOS.contains(extension)) {
                        logger.trace("Archivo detectado: {}", file);

                        Archivo doc = new Archivo();
                        doc.setRutaArchivo(file.toAbsolutePath().toString());
                        doc.setNombreArchivo(nombre);
                        doc.setTipoArchivo(extension);
                        doc.setTamano(attrs.size());
                        doc.setFechaModificacion(attrs.lastModifiedTime().toMillis());

                        archivosDetectados.add(doc);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.warn("No fue posible acceder al archivo: {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Error durante el rastreo del directorio: {}", rutaDirectorio, e);
        }

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        logger.info("Rastreo finalizado. {} archivos detectados", archivosDetectados.size());

        return archivosDetectados;
    }

    /**
     * Obtiene la extensión de un archivo.
     *
     * @param nombreArchivo nombre completo del archivo
     * @return extensión en minúsculas o una cadena vacía si el archivo
     *         no posee extensión válida
     */
    private String obtenerExtension(String nombreArchivo) {
        int lastIndex = nombreArchivo.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(lastIndex + 1).toLowerCase();
        }
        return "";
    }
}
