package org.example;

import org.example.service.ExtractorContenidoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(value = 60, unit = TimeUnit.SECONDS)
public class ExtractorContenidoServiceTest {

    private static final String CONTENIDO_ESPERADO = "Contenido de prueba para LocalFinder";

    private static ExtractorContenidoService extractor;
    private Path archivoTxt;

    @BeforeAll
    static void initExtractor() {
        extractor = new ExtractorContenidoService();
    }

    @BeforeEach
    void setUp() throws IOException {
        archivoTxt = Files.createTempFile("localfinder-test-", ".txt");
        Files.writeString(archivoTxt, CONTENIDO_ESPERADO);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (archivoTxt != null) {
            Files.deleteIfExists(archivoTxt);
        }
    }

    @Test
    @DisplayName("Debe extraer texto de un archivo TXT temporal con contenido conocido")
    void debeExtraerTextoDeArchivoTxt() {
        String resultado = extractor.extraerTexto(archivoTxt);

        assertNotNull(resultado, "El texto extraído no debe ser nulo");
        assertFalse(resultado.isEmpty(), "El texto extraído no debe estar vacío");
        assertTrue(
                resultado.contains(CONTENIDO_ESPERADO),
                "El texto extraído debe contener el contenido conocido del archivo"
        );
    }

    @Test
    @DisplayName("Debe devolver cadena vacía si el archivo no existe")
    void debeDevolverVacioSiArchivoNoExiste() {
        Path archivoInexistente = archivoTxt.getParent().resolve("archivo-que-no-existe.txt");

        String resultado = extractor.extraerTexto(archivoInexistente);

        assertEquals("", resultado, "Debe devolver cadena vacía cuando el archivo no existe");
    }

    @Test
    @DisplayName("Debe calcular un hash SHA-256 de exactamente 64 caracteres")
    void debeCalcularHashDe64Caracteres() {
        String hash = extractor.calcularHash(archivoTxt);

        assertNotNull(hash, "El hash no debe ser nulo");
        assertFalse(hash.isEmpty(), "El hash no debe estar vacío para un archivo válido");
        assertEquals(64, hash.length(), "El hash SHA-256 debe tener exactamente 64 caracteres hexadecimales");
        assertTrue(hash.matches("(?i)[0-9a-f]{64}"), "El hash debe contener solo caracteres hexadecimales");
    }

    @Test
    @DisplayName("El hash debe cambiar si cambia el contenido del archivo")
    void hashDebeCambiarSiCambiaContenido() throws IOException {
        String hashOriginal = extractor.calcularHash(archivoTxt);

        Files.writeString(archivoTxt, CONTENIDO_ESPERADO + " modificado");
        String hashModificado = extractor.calcularHash(archivoTxt);

        assertNotEquals(hashOriginal, hashModificado, "El hash debe cambiar cuando el contenido del archivo cambia");
    }

    @Test
    @DisplayName("El hash debe ser igual si el archivo no cambia")
    void hashDebeSerIgualSiArchivoNoCambia() {
        String primerHash = extractor.calcularHash(archivoTxt);
        String segundoHash = extractor.calcularHash(archivoTxt);

        assertEquals(primerHash, segundoHash, "El hash debe ser idéntico si el archivo no ha cambiado");
    }
}
