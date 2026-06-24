package org.example.service;

import org.example.service.interfaces.IndexadorLucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de indexar documentos y ejecutar búsquedas
 * utilizando Apache Lucene.
 *
 * <p>Los documentos se almacenan en un índice persistente ubicado
 * en el sistema de archivos. El contenido textual es analizado
 * mediante {@link StandardAnalyzer} y las búsquedas utilizan el
 * algoritmo de relevancia TF-IDF proporcionado por
 * {@link ClassicSimilarity}.</p>
 *
 * <p>Cada documento indexado se asocia a un identificador de archivo
 * proveniente de la base de datos, permitiendo relacionar los
 * resultados obtenidos con los metadatos almacenados en SQLite.</p>
 *
 * @author FiscalPro
 * @version 1.0
 */
public class IndexadorLuceneService implements IndexadorLucene {

    private static final Logger logger = LoggerFactory.getLogger(IndexadorLuceneService.class);

    /**
     * Ruta física donde se almacena el índice de Lucene.
     */
    private static final String RUTA_INDICE_LUCENE = "./lucene_index";

    /**
     * Directorio persistente utilizado por Lucene para almacenar
     * la estructura del índice.
     */
    private final Directory directorioIndice;

    /**
     * Analizador encargado de tokenizar y normalizar el contenido
     * textual durante la indexación y las búsquedas.
     */
    private final Analyzer analizador;

    /**
     * Inicializa el motor de indexación y búsqueda.
     *
     * @throws IOException si no es posible acceder o crear
     *                     el directorio del índice
     */
    public IndexadorLuceneService() throws IOException {
        this.directorioIndice = FSDirectory.open(Paths.get(RUTA_INDICE_LUCENE));

        this.analizador = new StandardAnalyzer();
    }

    /**
     * Indexa o actualiza un documento dentro del índice de Lucene.
     *
     * <p>Si el archivo ya existe en el índice, su contenido será
     * reemplazado por la nueva versión.</p>
     *
     * @param idArchivo identificador único del archivo
     * @param contenidoTexto contenido textual extraído del documento
     */
    @Override
    public void indexarArchivo(int idArchivo, String contenidoTexto) {
        logger.debug("Indexando archivo {}", idArchivo);

        IndexWriterConfig config = new IndexWriterConfig(analizador);

        config.setSimilarity(new ClassicSimilarity());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(directorioIndice, config)) {
            Document documento = new Document();

            documento.add(new StringField("id_archivo", String.valueOf(idArchivo), Field.Store.YES));

            documento.add(new TextField("contenido", contenidoTexto, Field.Store.YES));

            writer.updateDocument(new Term("id_archivo", String.valueOf(idArchivo)), documento);

            writer.commit();

            logger.info("Archivo {} indexado correctamente", idArchivo);
        } catch (IOException e) {
            logger.error("Error al indexar archivo {}", idArchivo, e);
        }
    }

    /**
     * Ejecuta una búsqueda textual sobre el índice.
     *
     * <p>Los resultados se devuelven ordenados según la puntuación
     * de relevancia calculada por Lucene.</p>
     *
     * @param consultaUsuario consulta introducida por el usuario
     * @param limiteResultados cantidad máxima de resultados
     *                         a recuperar
     * @return mapa donde la clave representa el identificador
     *         del archivo y el valor su puntuación de relevancia
     */
    @Override
    public Map<Integer, Float> buscar(String consultaUsuario, int limiteResultados) {
        Map<Integer, Float> resultados = new HashMap<>();

        if (consultaUsuario == null || consultaUsuario.trim().isEmpty()) {
            logger.debug("Consulta vacía ignorada");
            return resultados;
        }

        logger.debug("Ejecutando búsqueda: '{}'", consultaUsuario);

        try (DirectoryReader reader = DirectoryReader.open(directorioIndice)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            searcher.setSimilarity(new ClassicSimilarity());

            QueryParser parser = new QueryParser("contenido", analizador);
            String consultaProcesada = consultaUsuario.trim().contains(" ") ? consultaUsuario : consultaUsuario + "*";

            Query query = parser.parse(consultaProcesada);
            logger.trace("Consulta Lucene generada: {}", consultaProcesada);

            TopDocs topDocs = searcher.search(query, limiteResultados);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document docInfo = searcher.doc(scoreDoc.doc);
                int idArchivo = Integer.parseInt(docInfo.get("id_archivo"));
                float score = scoreDoc.score;

                resultados.put(idArchivo, score);
            }

            logger.info("Búsqueda '{}' devolvió {} resultados", consultaUsuario, resultados.size());
        } catch (IOException e) {
            logger.error("Error al acceder al índice Lucene", e);
        } catch (ParseException e) {
            logger.warn("Consulta inválida: '{}'", consultaUsuario, e);
        }

        return resultados;
    }
}
