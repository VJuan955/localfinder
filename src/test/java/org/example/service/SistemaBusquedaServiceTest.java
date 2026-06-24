package org.example.service;

import org.example.dao.*;
import org.example.service.interfaces.*;
import org.example.model.Archivo;
import org.example.model.Busqueda;
import org.example.model.Directorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas Unitarias Limpias para SistemaBusquedaService (SOLID / DIP)")
class SistemaBusquedaServiceTest {
    private Crawler crawlerMock;
    private ExtractorContenido extractorMock;
    private IndexadorLucene indexadorMock;

    private DirectorioDAO directorioDAOMock;
    private ArchivoDAO archivoDAOMock;
    private BusquedaDAO busquedaDAOMock;
    private ResultadoDAO resultadoDAOMock;

    private SistemaBusquedaService sistemaBusquedaService;

    @BeforeEach
    void setUp() {
        crawlerMock = mock(CrawlerService.class);
        extractorMock = mock(ExtractorContenidoService.class);
        indexadorMock = mock(IndexadorLuceneService.class);

        directorioDAOMock = mock(DirectorioDAO.class);
        archivoDAOMock = mock(ArchivoDAO.class);
        busquedaDAOMock = mock(BusquedaDAO.class);
        resultadoDAOMock = mock(ResultadoDAO.class);

        sistemaBusquedaService = new SistemaBusquedaService(
                crawlerMock,
                extractorMock,
                indexadorMock,
                directorioDAOMock,
                archivoDAOMock,
                busquedaDAOMock,
                resultadoDAOMock
        );
    }

    @Test
    @DisplayName("SincronizarIndice: Debería ignorar directorios con estado inactivo")
    void sincronizarIndiceDeberiaIgnorarDirectoriosInactivos() {
        Directorio dirInactivo = new Directorio();
        dirInactivo.setRutaDirectorio("/ruta/inactiva");
        dirInactivo.setEstado("inactivo");

        when(directorioDAOMock.obtenerTodos()).thenReturn(List.of(dirInactivo));

        sistemaBusquedaService.sincronizarIndice();

        verify(crawlerMock, never()).rastrearDirectorio(anyString());
        verifyNoInteractions(extractorMock, indexadorMock);
    }

    @Test
    @DisplayName("SincronizarIndice: Debería indexar un nuevo archivo encontrado en un directorio activo")
    void sincronizarIndiceDeberiaProcesarEIndexarArchivoNuevo() {
        Directorio dirActivo = new Directorio();
        dirActivo.setIdDirectorio(1);
        dirActivo.setRutaDirectorio("/documentos");
        dirActivo.setEstado("activo");

        Archivo nuevoArchivo = new Archivo();
        nuevoArchivo.setRutaArchivo("/documentos/contrato.pdf");
        nuevoArchivo.setNombreArchivo("contrato.pdf");

        when(directorioDAOMock.obtenerTodos()).thenReturn(List.of(dirActivo));
        when(crawlerMock.rastrearDirectorio("/documentos")).thenReturn(List.of(nuevoArchivo));
        when(archivoDAOMock.buscarPorRuta(nuevoArchivo.getRutaArchivo())).thenReturn(Optional.empty());

        when(extractorMock.calcularHash(any())).thenReturn("HASH-VALIDO-SHA256");
        when(extractorMock.extraerTexto(any())).thenReturn("Contenido legal del contrato");

        sistemaBusquedaService.sincronizarIndice();

        verify(archivoDAOMock, times(1)).insertar(eq(nuevoArchivo), eq(1));
        verify(indexadorMock, times(1)).indexarArchivo(anyInt(), eq("Contenido legal del contrato"));
    }

    @Test
    @DisplayName("RealizarBusqueda: Debería registrar la búsqueda y persistir el ranking si hay coincidencias")
    void realizarBusquedaDeberiaRegistrarResultadosYRanking() {
        String consulta = "Auditoría";
        Map<Integer, Float> coincidenciasSimuladas = Map.of(99, 3.8f);

        when(indexadorMock.buscar(consulta, 50)).thenReturn(coincidenciasSimuladas);

        Archivo archivoEnDb = new Archivo();
        archivoEnDb.setIdArchivo(99);
        archivoEnDb.setNombreArchivo("auditoria_2026.txt");
        when(archivoDAOMock.obtenerTodos()).thenReturn(List.of(archivoEnDb));

        List<Archivo> resultado = sistemaBusquedaService.realizarBusqueda(consulta);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("auditoria_2026.txt", resultado.get(0).getNombreArchivo());

        verify(busquedaDAOMock, times(1)).registrar(any(Busqueda.class));
        verify(resultadoDAOMock, times(1)).guardarResultados(anyList());
    }
}