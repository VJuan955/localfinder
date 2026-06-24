package org.example.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.dao.DatabaseManager;
import org.example.model.CoincidenciaIndice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de indexar documentos y ejecutar búsquedas
 * utilizando Apache Lucene con ranking BM25 y búsqueda multi-campo.
 */
public class IndexadorLuceneService {

    private static final Logger logger = LoggerFactory.getLogger(IndexadorLuceneService.class);

    private static final String[] CAMPOS_BUSQUEDA = {"nombre", "contenido"};
    private static final Map<String, Float> BOOST_CAMPOS = Map.of(
            "nombre", 4.0f,
            "contenido", 1.0f
    );

    private static final String RUTA_INDICE_LUCENE =
            DatabaseManager.getDataDir().resolve("lucene_index").toString();

    private final Directory directorioIndice;
    private final Analyzer analizador;
    private final BM25Similarity similitud = new BM25Similarity();

    public IndexadorLuceneService() throws IOException {
        this.directorioIndice = FSDirectory.open(Paths.get(RUTA_INDICE_LUCENE));
        this.analizador = crearAnalizador();
    }

    private static Analyzer crearAnalizador() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                StandardTokenizer tokenizer = new StandardTokenizer();
                TokenStream stream = new LowerCaseFilter(tokenizer);
                stream = new ASCIIFoldingFilter(stream);
                return new TokenStreamComponents(tokenizer, stream);
            }
        };
    }

    public void indexarArchivo(int idArchivo, String nombreArchivo, String contenidoTexto) {
        logger.debug("Indexando archivo {} ({})", idArchivo, nombreArchivo);

        IndexWriterConfig config = new IndexWriterConfig(analizador);
        config.setSimilarity(similitud);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        String nombre = nombreArchivo != null ? nombreArchivo : "";
        String contenido = contenidoTexto != null ? contenidoTexto : "";

        try (IndexWriter writer = new IndexWriter(directorioIndice, config)) {
            Document documento = new Document();
            documento.add(new StringField("id_archivo", String.valueOf(idArchivo), Field.Store.YES));
            documento.add(new TextField("nombre", nombre, Field.Store.YES));
            documento.add(new TextField("contenido", contenido, Field.Store.YES));

            writer.updateDocument(new Term("id_archivo", String.valueOf(idArchivo)), documento);
            writer.commit();

            logger.info("Archivo {} indexado correctamente", idArchivo);
        } catch (IOException e) {
            logger.error("Error al indexar archivo {}", idArchivo, e);
        }
    }

    /**
     * Ejecuta una búsqueda y devuelve coincidencias ordenadas por relevancia (mayor score primero).
     */
    public List<CoincidenciaIndice> buscar(String consultaUsuario, int limiteResultados) {
        List<CoincidenciaIndice> resultados = new ArrayList<>();

        if (consultaUsuario == null || consultaUsuario.trim().isEmpty()) {
            return resultados;
        }

        String consulta = consultaUsuario.trim();
        logger.debug("Ejecutando búsqueda: '{}'", consulta);

        try {
            if (!DirectoryReader.indexExists(directorioIndice)) {
                logger.info("Índice Lucene vacío. Sincroniza directorios desde Configuración.");
                return resultados;
            }
        } catch (IOException e) {
            logger.info("Índice Lucene no disponible. Sincroniza directorios desde Configuración.");
            return resultados;
        }

        try (DirectoryReader reader = DirectoryReader.open(directorioIndice)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(similitud);

            Query query = construirConsulta(consulta);
            TopDocs topDocs = searcher.search(query, limiteResultados);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document docInfo = searcher.storedFields().document(scoreDoc.doc);
                int idArchivo = Integer.parseInt(docInfo.get("id_archivo"));
                resultados.add(new CoincidenciaIndice(idArchivo, scoreDoc.score));
            }

            logger.info("Búsqueda '{}' devolvió {} resultados", consulta, resultados.size());
        } catch (IOException e) {
            logger.error("Error al acceder al índice Lucene", e);
        } catch (ParseException e) {
            logger.warn("Consulta inválida: '{}'", consulta, e);
        }

        return resultados;
    }

    /**
     * Construye una consulta que prioriza:
     * - coincidencias en el nombre del archivo (boost x4)
     * - frases exactas en contenido/nombre (boost x3)
     * - todos los términos presentes (operador AND)
     */
    private Query construirConsulta(String consulta) throws ParseException {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                CAMPOS_BUSQUEDA,
                analizador,
                BOOST_CAMPOS
        );
        parser.setDefaultOperator(QueryParser.Operator.AND);

        String consultaEscapada = escaparTerminos(consulta);
        Query terminosQuery = parser.parse(consultaEscapada);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new BoostQuery(terminosQuery, 1.0f), BooleanClause.Occur.SHOULD);

        if (consulta.contains(" ")) {
            Query fraseQuery = parser.parse("\"" + consultaEscapada + "\"");
            builder.add(new BoostQuery(fraseQuery, 3.0f), BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    private String escaparTerminos(String consulta) {
        String[] terminos = consulta.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terminos.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(QueryParser.escape(terminos[i]));
        }
        return sb.toString();
    }
}
