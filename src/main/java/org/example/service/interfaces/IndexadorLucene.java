package org.example.service.interfaces;
import java.util.Map;

public interface IndexadorLucene {
    void indexarArchivo(int idArchivo, String textoPlano);
    Map<Integer, Float> buscar(String consulta, int limite);
}