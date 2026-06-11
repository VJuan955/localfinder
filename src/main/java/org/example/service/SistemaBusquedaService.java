package org.example.service;

import org.example.dao.*;
import org.example.dao.impl.*;
import org.example.model.Archivo;
import org.example.model.Busqueda;
import org.example.model.Directorio;
import org.example.model.Resultado;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio principal del sistema de búsqueda documental.
 *
 * <p>Coordina los procesos de rastreo, extracción de contenido,
 * indexación y recuperación de documentos.</p>
 *
 * <p>Actúa como capa de orquestación entre los servicios de
 * procesamiento documental, Apache Lucene y la capa de persistencia
 * basada en SQLite.</p>
 *
 * <p>También registra el historial de búsquedas y almacena los
 * resultados obtenidos para consultas posteriores.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class SistemaBusquedaService {

    private static final Logger logger = LoggerFactory.getLogger(SistemaBusquedaService.class);

    private final CrawlerService crawler;
    private final ExtractorContenidoService extractor;
    private final IndexadorLuceneService indexador;

    private final DirectorioDAO directorioDAO;
    private final ArchivoDAO archivoDAO;
    private final BusquedaDAO busquedaDAO;
    private final ResultadoDAO resultadoDAO;

    /**
     * Inicializa todos los servicios y componentes necesarios para
     * el funcionamiento del sistema de búsqueda.
     *
     * @throws IOException si ocurre un error al inicializar
     *                     el índice de Lucene
     */
    public SistemaBusquedaService() throws IOException {
        this.crawler = new CrawlerService();
        this.extractor = new ExtractorContenidoService();
        this.indexador = new IndexadorLuceneService();

        this.directorioDAO = new DirectorioDAOImpl();
        this.archivoDAO = new ArchivoDAOImpl();
        this.busquedaDAO = new BusquedaDAOImpl();
        this.resultadoDAO = new ResultadoDAOImpl();
    }

    /**
     * Sincroniza el índice de búsqueda con todos los directorios
     * activos registrados en el sistema.
     *
     * <p>Para cada directorio activo se realiza un rastreo completo
     * del contenido, verificando cambios mediante hashes SHA-256 e
     * indexando únicamente los archivos nuevos o modificados.</p>
     */
    public void sincronizarIndice() {
        logger.info("Iniciando sincronización del índice");

        List<Directorio> directorios = directorioDAO.obtenerTodos();

        for (Directorio dir : directorios) {
            if ("activo".equalsIgnoreCase(dir.getEstado())) {
                logger.info("Procesando directorio: {}", dir.getRutaDirectorio());
                procesarDirectorio(dir);
            }
        }
        logger.info("Sincronización completada");
    }

    /**
     * Procesa un directorio específico realizando detección de cambios
     * e indexación incremental de documentos.
     *
     * @param directorio directorio que será procesado
     */
    private void procesarDirectorio(Directorio directorio) {
        logger.debug("Analizando directorio {}", directorio.getRutaDirectorio());

        List<Archivo> archivosEncontrados = crawler.rastrearDirectorio(directorio.getRutaDirectorio());
        logger.info("{} archivos encontrados en {}", archivosEncontrados.size(), directorio.getRutaDirectorio());

        for (Archivo docActual : archivosEncontrados) {
            Optional<Archivo> docExistente = archivoDAO.buscarPorRuta(docActual.getRutaArchivo());

            boolean necesitaIndexacion = false;
            String hashActual = extractor.calcularHash(Paths.get(docActual.getRutaArchivo()));
            docActual.setHashArchivo(hashActual);

            if (docExistente.isEmpty()) {
                necesitaIndexacion = true;
                logger.debug("Nuevo archivo detectado: {}", docActual.getRutaArchivo());
                archivoDAO.insertar(docActual, directorio.getIdDirectorio());
                docActual = archivoDAO.buscarPorRuta(docActual.getRutaArchivo()).orElse(docActual);
            } else {
                Archivo dbDoc = docExistente.get();
                if (hashActual != null && !hashActual.equals(dbDoc.getHashArchivo())) {
                    necesitaIndexacion = true;
                    logger.debug("Archivo modificado detectado: {}", docActual.getRutaArchivo());
                    docActual.setIdArchivo(dbDoc.getIdArchivo());
                    archivoDAO.actualizar(docActual);
                }
            }

            if (necesitaIndexacion) {
                logger.info("Indexando archivo: {}", docActual.getNombreArchivo());
                String textoPlano = extractor.extraerTexto(Paths.get(docActual.getRutaArchivo()));
                indexador.indexarArchivo(docActual.getIdArchivo(), textoPlano);
            }
        }
    }

    /**
     * Ejecuta una búsqueda textual sobre el índice documental.
     *
     * <p>Los resultados obtenidos se almacenan en el historial
     * de búsquedas junto con su ranking de relevancia.</p>
     *
     * @param consultaUsuario consulta introducida por el usuario
     * @return lista de archivos coincidentes ordenados según
     *         la relevancia calculada por Lucene
     */
    public List<Archivo> realizarBusqueda(String consultaUsuario) {
        logger.info("Ejecutando búsqueda: '{}'", consultaUsuario);

        List<Archivo> archivosResultado = new ArrayList<>();

        Map<Integer, Float> coincidencias = indexador.buscar(consultaUsuario, 50);

        if (coincidencias.isEmpty()) {
            logger.info("La búsqueda '{}' no produjo resultados", consultaUsuario);
            return archivosResultado;
        }

        Busqueda registroBusqueda = new Busqueda();
        registroBusqueda.setFiltrosAplicados("{}");
        registroBusqueda.setFechaBusqueda(System.currentTimeMillis());
        busquedaDAO.registrar(registroBusqueda);
        logger.debug("Búsqueda registrada con ID {}", registroBusqueda.getIdBusqueda());

        List<Resultado> loteResultados = new ArrayList<>();
        int posicion = 1;

        List<Archivo> todosLosArchivos = archivoDAO.obtenerTodos();

        for (Map.Entry<Integer, Float> entry : coincidencias.entrySet()) {
            int idArchivo = entry.getKey();
            float score = entry.getValue();

            Optional<Archivo> match = todosLosArchivos.stream()
                    .filter(a -> a.getIdArchivo() == idArchivo)
                    .findFirst();

            if (match.isPresent()) {
                archivosResultado.add(match.get());

                Resultado res = new Resultado();
                res.setPosicionResultado(posicion++);
                res.setRanking(score);
                res.setIdBusqueda(registroBusqueda.getIdBusqueda());
                res.setIdArchivo(idArchivo);
                loteResultados.add(res);

                logger.trace("Coincidencia encontrada. Archivo={} Score={}", idArchivo, score);
            }
        }

        if (!loteResultados.isEmpty()) {
            resultadoDAO.guardarResultados(loteResultados);
        }

        logger.info("Búsqueda '{}' completada con {} resultados", consultaUsuario, archivosResultado.size());
        return archivosResultado;
    }
}
