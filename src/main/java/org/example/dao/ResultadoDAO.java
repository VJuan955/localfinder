package org.example.dao;

import org.example.model.Resultado;
import java.util.List;

/**
 * Define las operaciones de persistencia para los resultados
 * obtenidos en una búsqueda.
 *
 * @author michellsalazar
 * @version 1.0
 */
public interface ResultadoDAO {

    /**
     * Guarda una colección de resultados asociados a una búsqueda.
     *
     * @param resultados resultados a almacenar
     */
    void guardarResultados(List<Resultado> resultados);

    /**
     * Recupera los resultados de una búsqueda específica.
     *
     * @param idBusqueda identificador de la búsqueda
     * @return lista de resultados asociados
     */
    List<Resultado> obtenerPorBusqueda(int idBusqueda);
}
