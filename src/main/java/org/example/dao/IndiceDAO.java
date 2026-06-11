package org.example.dao;

import org.example.model.Indice;
import java.util.List;

/**
 * Define las operaciones de persistencia para las entradas del índice
 * de búsqueda.
 *
 * @author VJuan955
 * @version 1.0
 */
public interface IndiceDAO {

    /**
     * Inserta una nueva entrada en el índice.
     *
     * @param indice entrada de índice a registrar
     */
    void insertar(Indice indice);

    /**
     * Recupera las entradas asociadas a un término específico.
     *
     * @param idTermino identificador del término
     * @return lista de entradas encontradas
     */
    List<Indice> buscarPorTermino(int idTermino);
}
