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

public class SistemaBusquedaService {
    private final CrawlerService crawler;
    private final ExtractorContenidoService extractor;
    private final IndexadorLuceneService indexador;

    private final DirectorioDAO directorioDAO;
    private final ArchivoDAO archivoDAO;
    private final BusquedaDAO busquedaDAO;
    private final ResultadoDAO resultadoDAO;

    public SistemaBusquedaService() throws IOException {
        this.crawler = new CrawlerService();
        this.extractor = new ExtractorContenidoService();
        this.indexador = new IndexadorLuceneService();

        this.directorioDAO = new DirectorioDAOImpl();
        this.archivoDAO = new ArchivoDAOImpl();
        this.busquedaDAO = new BusquedaDAOImpl();
        this.resultadoDAO = new ResultadoDAOImpl();
    }

    public void sincronizarIndice() {
        List<Directorio> directorios = directorioDAO.obtenerTodos();

        for (Directorio dir : directorios) {
            if ("activo".equalsIgnoreCase(dir.getEstado())) {
                System.out.println("Sincronizando directorio: " + dir.getRutaDirectorio());
                procesarDirectorio(dir);
            }
        }
        System.out.println("Sincronización completada.");
    }

    private void procesarDirectorio(Directorio directorio) {
        List<Archivo> archivosEncontrados = crawler.rastrearDirectorio(directorio.getRutaDirectorio());

        for (Archivo docActual : archivosEncontrados) {
            Optional<Archivo> docExistente = archivoDAO.buscarPorRuta(docActual.getRutaArchivo());

            boolean necesitaIndexacion = false;
            String hashActual = extractor.calcularHash(Paths.get(docActual.getRutaArchivo()));
            docActual.setHashArchivo(hashActual);

            if (docExistente.isEmpty()) {
                necesitaIndexacion = true;
                archivoDAO.insertar(docActual, directorio.getIdDirectorio());
                docActual = archivoDAO.buscarPorRuta(docActual.getRutaArchivo()).orElse(docActual);
            } else {
                Archivo dbDoc = docExistente.get();
                if (hashActual != null && !hashActual.equals(dbDoc.getRutaArchivo())) {
                    necesitaIndexacion = true;
                    docActual.setIdArchivo(dbDoc.getIdArchivo());
                    archivoDAO.actualizar(docActual);
                }
            }

            if (necesitaIndexacion) {
                System.out.println("Indexando: " + docActual.getNombreArchivo());
                String textoPlano = extractor.extraerTexto(Paths.get(docActual.getRutaArchivo()));
                indexador.indexarArchivo(docActual.getIdArchivo(), textoPlano);
            }
        }
    }

    public List<Archivo> realizarBusqueda(String consultaUsuario) {
        List<Archivo> archivosResultado = new ArrayList<>();

        Map<Integer, Float> coincidencias = indexador.buscar(consultaUsuario, 50);

        if (coincidencias.isEmpty()) {
            return archivosResultado;
        }

        Busqueda registroBusqueda = new Busqueda();
        registroBusqueda.setFiltrosAplicados("{}");
        registroBusqueda.setFechaBusqueda(System.currentTimeMillis());
        busquedaDAO.registrar(registroBusqueda);

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
                res.setIdBusqueda(idArchivo);
                loteResultados.add(res);
            }
        }

        if (!loteResultados.isEmpty()) {
            resultadoDAO.guardarResultados(loteResultados);
        }

        return archivosResultado;
    }
}
