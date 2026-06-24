package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para ExtractorContenidoService (JUnit 6)")
class ExtractorContenidoServiceTest {

    private ExtractorContenidoService extractorService;

    @BeforeEach
    void setUp() {
        extractorService = new ExtractorContenidoService();
    }

    @Nested
    @DisplayName("Pruebas para Extracción de Texto")
    class ExtraccionTextoTests {

        @Test
        @DisplayName("Debería extraer el texto plano de un archivo correctamente")
        void deberiaExtraerTextoDeArchivoValido(@TempDir Path tempDir) throws IOException {
            Path archivoTexto = tempDir.resolve("nota.txt");
            String contenidoOriginal = "Hola FiscalPro, este es un texto de prueba para Tika.";
            Files.writeString(archivoTexto, contenidoOriginal);

            String resultado = extractorService.extraerTexto(archivoTexto);

            assertNotNull(resultado);

            assertTrue(resultado.contains(contenidoOriginal), "El texto extraído no coincide con el original");
        }

        @Test
        @DisplayName("Debería retornar cadena vacía si el archivo no existe")
        void deberiaRetornarVacioSiArchivoNoExiste() {

            Path archivoInexistente = Path.of("ruta/fantasma/documento.txt");

            String resultado = extractorService.extraerTexto(archivoInexistente);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty(), "Debería retornar un String vacío al fallar");
        }
    }

    @Nested
    @DisplayName("Pruebas para Cálculo de Hash SHA-256")
    class CalculoHashTests {

        @Test
        @DisplayName("Debería calcular el hash SHA-256 exacto de un archivo")
        void deberiaCalcularHashCorrecto(@TempDir Path tempDir) throws IOException {

            Path archivoHash = tempDir.resolve("hash-test.txt");

            Files.writeString(archivoHash, "abc");

            String hashEsperado = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

            String hashCalculado = extractorService.calcularHash(archivoHash);

            assertNotNull(hashCalculado);
            assertEquals(hashEsperado, hashCalculado, "El hash SHA-256 calculado no es el correcto");
        }

        @Test
        @DisplayName("Debería retornar cadena vacía si el archivo para hash no existe")
        void deberiaRetornarVacioAlCalcularHashDeArchivoInexistente() {
            Path archivoInexistente = Path.of("ruta/fantasma/secreto.docx");

            String resultado = extractorService.calcularHash(archivoInexistente);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty(), "Debería retornar un String vacío si no puede leer el archivo");
        }
    }
}