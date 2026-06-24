package org.example.service;

import org.example.model.Archivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para CrawlerService (JUnit 6)")
class CrawlerServiceTest {

    private CrawlerService crawlerService;

    @BeforeEach
    void setUp() {
        crawlerService = new CrawlerService();
    }

    @Test
    @DisplayName("Debería retornar lista vacía si el directorio no existe")
    void deberiaRetornarVacioSiDirectorioNoExiste() {
        String rutaInvalida = "/ruta/que/no/existe/en/el/sistema";

        List<Archivo> resultado = crawlerService.rastrearDirectorio(rutaInvalida);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debería estar vacía para un directorio inexistente");
    }

    @Test
    @DisplayName("Debería detectar solo archivos con extensiones permitidas (pdf, txt, docx)")
    void deberiaDetectarSoloFormatosPermitidos(@TempDir Path directorioTemporal) throws IOException {
        Files.createFile(directorioTemporal.resolve("documento1.pdf"));
        Files.createFile(directorioTemporal.resolve("nota.txt"));
        Files.createFile(directorioTemporal.resolve("informe.docx"));
        Files.createFile(directorioTemporal.resolve("imagen.png")); // No permitido
        Files.createFile(directorioTemporal.resolve("sinExtension")); // No permitido

        List<Archivo> resultado = crawlerService.rastrearDirectorio(directorioTemporal.toString());

        assertEquals(3, resultado.size(), "Debería haber detectado exactamente 3 archivos");

        boolean contienePdf = resultado.stream().anyMatch(a -> a.getTipoArchivo().equals("pdf"));
        boolean contieneTxt = resultado.stream().anyMatch(a -> a.getTipoArchivo().equals("txt"));
        boolean contieneDocx = resultado.stream().anyMatch(a -> a.getTipoArchivo().equals("docx"));
        boolean contienePng = resultado.stream().anyMatch(a -> a.getTipoArchivo().equals("png"));

        assertTrue(contienePdf, "Debería incluir el archivo PDF");
        assertTrue(contieneTxt, "Debería incluir el archivo TXT");
        assertTrue(contieneDocx, "Debería incluir el archivo DOCX");
        assertFalse(contienePng, "NO debería incluir el archivo PNG");
    }

    @Test
    @DisplayName("Debería mapear correctamente las propiedades del archivo encontrado")
    void deberiaMapearPropiedadesCorrectamente(@TempDir Path directorioTemporal) throws IOException {
        Path archivoDummy = directorioTemporal.resolve("test.pdf");
        String contenido = "Contenido de prueba";
        Files.writeString(archivoDummy, contenido);

        List<Archivo> resultado = crawlerService.rastrearDirectorio(directorioTemporal.toString());

        assertEquals(1, resultado.size());
        Archivo archivoMapeado = resultado.get(0);

        assertAll("Verificación de mapeo de propiedades",
                () -> assertEquals("test.pdf", archivoMapeado.getNombreArchivo()),
                () -> assertEquals("pdf", archivoMapeado.getTipoArchivo()),
                () -> assertEquals(archivoDummy.toAbsolutePath().toString(), archivoMapeado.getRutaArchivo()),
                () -> assertTrue(archivoMapeado.getTamano() > 0, "El tamaño debería ser mayor a 0"),
                () -> assertTrue(archivoMapeado.getFechaModificacion() > 0, "Debería registrar la fecha de modificación")
        );
    }

    @Test
    @DisplayName("Debería rastrear de forma recursiva (subcarpetas)")
    void deberiaRastrearSubdirectoriosRecursivamente(@TempDir Path directorioTemporal) throws IOException {
        Path subcarpeta = Files.createDirectory(directorioTemporal.resolve("subcarpeta"));
        Path subSubcarpeta = Files.createDirectory(subcarpeta.resolve("profundo"));

        Files.createFile(directorioTemporal.resolve("raiz.txt"));
        Files.createFile(subcarpeta.resolve("medio.pdf"));
        Files.createFile(subSubcarpeta.resolve("final.docx"));

        List<Archivo> resultado = crawlerService.rastrearDirectorio(directorioTemporal.toString());

        assertEquals(3, resultado.size(), "Debería encontrar los archivos en todos los niveles de profundidad");
    }
}