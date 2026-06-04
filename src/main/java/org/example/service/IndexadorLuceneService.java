package org.example.service;

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

public class IndexadorLuceneService {
    private static final String RUTA_INDICE_LUCENE = "./lucene_index";
    private final Directory directorioIndice;
    private final Analyzer analizador;

    public IndexadorLuceneService() throws IOException {
        this.directorioIndice = FSDirectory.open(Paths.get(RUTA_INDICE_LUCENE));

        this.analizador = new StandardAnalyzer();
    }

    public void indexarArchivo(int idArchivo, String contenidoTexto) {
        IndexWriterConfig config = new IndexWriterConfig(analizador);

        config.setSimilarity(new ClassicSimilarity());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(directorioIndice, config)) {
            Document documento = new Document();

            documento.add(new StringField("id_archivo", String.valueOf(idArchivo), Field.Store.YES));

            documento.add(new TextField("contenido", contenidoTexto, Field.Store.YES));

            writer.updateDocument(new Term("id_archivo", String.valueOf(idArchivo)), documento);

            writer.commit();
        } catch (IOException e) {
            System.err.println("Error al indexar en Lucene el archivo ID: " + idArchivo + " - " + e.getMessage());
        }
    }

    public Map<Integer, Float> buscar(String consultaUsuario, int limiteResultados) {
        Map<Integer, Float> resultados = new HashMap<>();

        if (consultaUsuario == null || consultaUsuario.trim().isEmpty()) {
            return resultados;
        }

        try (DirectoryReader reader = DirectoryReader.open(directorioIndice)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            searcher.setSimilarity(new ClassicSimilarity());

            QueryParser parser = new QueryParser("contenido", analizador);
            String consultaProcesada = consultaUsuario.trim().contains(" ") ? consultaUsuario : consultaUsuario + "*";

            Query query = parser.parse(consultaProcesada);

            TopDocs topDocs = searcher.search(query, limiteResultados);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document docInfo = searcher.doc(scoreDoc.doc);
                int idArchivo = Integer.parseInt(docInfo.get("id_archivo"));
                float score = scoreDoc.score;

                resultados.put(idArchivo, score);
            }
        } catch (IOException e) {
            System.err.println("No se encontró el directorio del índice o está bloqueado: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Sintaxis de búsqueda inválida: " + e.getMessage());
        }

        return resultados;
    }
}
