package org.example.dao;

import org.example.model.Busqueda;
import java.util.List;

/**
 * Define las operaciones de persistencia relacionadas con las búsquedas
 * realizadas por los usuarios.
 *
 * @author VJuan955
 * @version 1.0
 */
public interface BusquedaDAO {

    /**
     * Registra una búsqueda en el historial.
     *
     * @param busqueda búsqueda realizada
     */
    void registrar(Busqueda busqueda);

    /**
     * Recupera el historial completo de búsquedas.
     *
     * @return lista de búsquedas registradas
     */
    List<Busqueda> obtenerHistorial();
}
