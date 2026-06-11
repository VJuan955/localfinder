package org.example.dao;

import org.example.model.Termino;
import java.util.Optional;

/**
 * Define las operaciones de persistencia para los términos
 * indexados por el sistema.
 *
 * @author michellsalazar
 * @version 1.0
 */
public interface TerminoDAO {

    /**
     * Inserta un término si no existe y devuelve su identificador.
     *
     * @param palabra término a registrar
     * @return identificador del término existente o recién creado
     */
    int insertarOObtener(String palabra);

    /**
     * Busca un término por su representación textual.
     *
     * @param palabra palabra a consultar
     * @return un {@code Optional} con el término encontrado,
     *         o vacío si no existe
     */
    Optional<Termino> buscarPorPalabra(String palabra);

    /**
     * Incrementa la frecuencia global asociada a un término.
     *
     * @param idTermino identificador del término
     * @param incremento valor que se sumará a la frecuencia actual
     */
    void incrementarFrecuenciaGlobal(int idTermino, int incremento);
}
