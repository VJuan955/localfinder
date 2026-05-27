package org.example.service;

import org.example.model.Archivo;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CrawlerService {
    private static final Set<String> FORMATOS_PERMITIDOS = Set.of("pdf", "txt", "docx");

    public List<Archivo> rastrearDirectorio(String rutaDirectorio) {
        List<Archivo> archivosDetectados = new ArrayList<>();
        Path directorioRaiz = Paths.get(rutaDirectorio);

        if (!Files.exists(directorioRaiz) || !Files.isReadable(directorioRaiz)) {
            System.err.println("Directorio inválido o acceso denegado: " + rutaDirectorio);
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
                    System.err.println("Advertencia - No se pudo acceder a: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error crítico al explorar el directorio: " + e.getMessage());
        }

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        return archivosDetectados;
    }

    private String obtenerExtension(String nombreArchivo) {
        int lastIndex = nombreArchivo.lastIndexOf('.');
        if (lastIndex > 0 && lastIndex < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(lastIndex + 1).toLowerCase();
        }
        return "";
    }
}
