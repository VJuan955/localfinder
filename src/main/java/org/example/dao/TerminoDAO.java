package org.example.dao;

import org.example.model.Termino;
import java.util.Optional;

public interface TerminoDAO {
    int insertarOObtener(String palabra);
    Optional<Termino> buscarPorPalabra(String palabra);
    void incrementarFrecuenciaGlobal(int idTermino, int incremento);
}
