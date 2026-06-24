package org.example;

import org.example.model.Archivo;
import org.example.service.CrawlerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrawlerServiceTest {

    private CrawlerService crawler;
    private Path directorioTemp;

    @BeforeEach
    void setUp() throws IOException {
        crawler = new CrawlerService();
        directorioTemp = Files.createTempDirectory("localfinder-crawler-");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (directorioTemp != null && Files.exists(directorioTemp)) {
            try (var rutas = Files.walk(directorioTemp)) {
                rutas.sorted(Comparator.reverseOrder()).forEach(ruta -> {
                    try {
                        Files.deleteIfExists(ruta);
                    } catch (IOException e) {
                        throw new RuntimeException("No se pudo eliminar: " + ruta, e);
                    }
                });
            }
        }
    }

    @Test
    @DisplayName("Debe encontrar exactamente los archivos PDF, TXT y DOCX de un directorio temporal")
    void debeEncontrarArchivosPermitidos() throws IOException {
        Files.createFile(directorioTemp.resolve("documento.txt"));
        Files.createFile(directorioTemp.resolve("informe.pdf"));
        Files.createFile(directorioTemp.resolve("tesis.docx"));
        Files.createFile(directorioTemp.resolve("imagen.png"));
        Files.createFile(directorioTemp.resolve("foto.jpg"));

        List<Archivo> resultados = crawler.rastrearDirectorio(directorioTemp.toString());

        assertEquals(3, resultados.size(), "Debe encontrar exactamente 3 archivos (pdf, txt, docx)");

        Set<String> tiposEncontrados = resultados.stream()
                .map(Archivo::getTipoArchivo)
                .collect(Collectors.toSet());

        assertTrue(tiposEncontrados.contains("txt"), "Debe incluir archivos TXT");
        assertTrue(tiposEncontrados.contains("pdf"), "Debe incluir archivos PDF");
        assertTrue(tiposEncontrados.contains("docx"), "Debe incluir archivos DOCX");
    }

    @Test
    @DisplayName("No debe incluir archivos de otros formatos (PNG, JPG, etc.)")
    void noDebeIncluirOtrosFormatos() throws IOException {
        Files.createFile(directorioTemp.resolve("imagen.png"));
        Files.createFile(directorioTemp.resolve("foto.jpg"));
        Files.createFile(directorioTemp.resolve("notas.txt"));

        List<Archivo> resultados = crawler.rastrearDirectorio(directorioTemp.toString());

        assertEquals(1, resultados.size(), "Solo debe incluir el archivo TXT");
        assertEquals("txt", resultados.get(0).getTipoArchivo(), "El único archivo debe ser TXT");
    }

    @Test
    @DisplayName("Cada archivo encontrado debe tener nombre, ruta y tipo no nulos")
    void archivosDebenTenerMetadatosCompletos() throws IOException {
        Files.createFile(directorioTemp.resolve("prueba.txt"));

        List<Archivo> resultados = crawler.rastrearDirectorio(directorioTemp.toString());

        assertEquals(1, resultados.size(), "Debe encontrar un archivo");

        Archivo archivo = resultados.get(0);
        assertNotNull(archivo.getNombreArchivo(), "El nombre del archivo no debe ser nulo");
        assertNotNull(archivo.getRutaArchivo(), "La ruta del archivo no debe ser nula");
        assertNotNull(archivo.getTipoArchivo(), "El tipo del archivo no debe ser nulo");
        assertEquals("prueba.txt", archivo.getNombreArchivo(), "El nombre debe coincidir con el archivo creado");
    }

    @Test
    @DisplayName("El tamaño de cada archivo debe ser mayor o igual a 0")
    void tamanoDebeSerMayorOIgualACero() throws IOException {
        Files.writeString(directorioTemp.resolve("con-contenido.txt"), "texto de prueba");
        Files.createFile(directorioTemp.resolve("vacio.pdf"));

        List<Archivo> resultados = crawler.rastrearDirectorio(directorioTemp.toString());

        assertEquals(2, resultados.size(), "Debe encontrar dos archivos");

        for (Archivo archivo : resultados) {
            assertTrue(archivo.getTamano() >= 0, "El tamaño del archivo debe ser mayor o igual a 0");
        }
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el directorio no existe")
    void debeDevolverVacioSiDirectorioNoExiste() {
        String rutaInexistente = directorioTemp.resolve("no-existe").toString();

        List<Archivo> resultados = crawler.rastrearDirectorio(rutaInexistente);

        assertTrue(resultados.isEmpty(), "Debe devolver lista vacía si el directorio no existe");
    }

    @Test
    @DisplayName("Debe devolver lista vacía si el directorio está vacío")
    void debeDevolverVacioSiDirectorioVacio() {
        List<Archivo> resultados = crawler.rastrearDirectorio(directorioTemp.toString());

        assertTrue(resultados.isEmpty(), "Debe devolver lista vacía si el directorio no contiene archivos válidos");
    }
}
