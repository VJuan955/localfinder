package org.example.service;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para IndexadorLuceneService (JUnit 6)")
class IndexadorLuceneServiceTest {

    private IndexadorLuceneService indexadorService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws Exception {
        indexadorService = new IndexadorLuceneService();

        Field directorioField = IndexadorLuceneService.class.getDeclaredField("directorioIndice");
        directorioField.setAccessible(true);

        Directory directorioTemporalLucene = FSDirectory.open(tempDir);
        directorioField.set(indexadorService, directorioTemporalLucene);
    }

    @Test
    @DisplayName("Debería indexar un documento y permitir su búsqueda inmediata")
    void deberiaIndexarYBuscarDocumento() {
        int idArchivo = 101;
        String contenido = "El contratista de FiscalPro presentará los informes financieros el próximo lunes.";

        indexadorService.indexarArchivo(idArchivo, contenido);

        Map<Integer, Float> resultados = indexadorService.buscar("informes", 5);

        assertNotNull(resultados);
        assertEquals(1, resultados.size(), "Debería haber encontrado exactamente 1 documento");
        assertTrue(resultados.containsKey(idArchivo), "El mapa de resultados debería contener el id del archivo indexado");
        assertTrue(resultados.get(idArchivo) > 0, "El score de relevancia debería ser mayor a cero");
    }

    @Test
    @DisplayName("Debería retornar mapa vacío si la consulta no coincide con ningún documento")
    void deberiaRetornarVacioSiNoHayCoincidencias() {
        indexadorService.indexarArchivo(42, "Cualquier contenido sin importancia");

        Map<Integer, Float> resultados = indexadorService.buscar("criptografía", 10);

        assertNotNull(resultados);
        assertTrue(resultados.isEmpty(), "No debería retornar resultados para términos inexistentes");
    }

    @Test
    @DisplayName("Debería retornar un mapa vacío si la consulta es nula o vacía")
    void deberiaManejarConsultasVacias() {
        indexadorService.indexarArchivo(1, "Contenido aleatorio");

        assertTrue(indexadorService.buscar(null, 5).isEmpty());
        assertTrue(indexadorService.buscar("   ", 5).isEmpty());
    }

    @Test
    @DisplayName("Debería soportar búsquedas con comodín implícito (Wildcard *) para palabras únicas")
    void deberiaSoportarBusquedaWildcard() {
        indexadorService.indexarArchivo(202, "Automatización de procesos legales");

        Map<Integer, Float> resultados = indexadorService.buscar("Auto", 5);

        assertEquals(1, resultados.size());
        assertTrue(resultados.containsKey(202));
    }

    @Test
    @DisplayName("Debería actualizar el documento en lugar de duplicarlo si se usa el mismo ID")
    void deberiaActualizarDocumentoExistente() {
        int idArchivoCompartido = 500;
        indexadorService.indexarArchivo(idArchivoCompartido, "Texto antiguo que será reemplazado");

        indexadorService.indexarArchivo(idArchivoCompartido, "Texto nuevo con la palabra clave Sol");

        Map<Integer, Float> busquedaTextoAntiguo = indexadorService.buscar("antiguo", 5);
        Map<Integer, Float> busquedaTextoNuevo = indexadorService.buscar("Sol", 5);

        assertAll("Verificación de comportamiento updateDocument de Lucene",
                () -> assertTrue(busquedaTextoAntiguo.isEmpty(), "El texto antiguo ya no debería ser trackeable"),
                () -> assertEquals(1, busquedaTextoNuevo.size(), "El nuevo texto debería estar indexado correctamente"),
                () -> assertTrue(busquedaTextoNuevo.containsKey(idArchivoCompartido))
        );
    }
}