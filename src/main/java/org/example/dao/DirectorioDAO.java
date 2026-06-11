package org.example.dao;

import org.example.model.Directorio;
import java.util.List;

/**
 * Define las operaciones de persistencia para los directorios
 * monitoreados por el sistema.
 *
 * @author VJuan955
 * @version 1.0
 */
public interface DirectorioDAO {

    /**
     * Registra un nuevo directorio.
     *
     * @param directorio directorio a almacenar
     */
    void insertar(Directorio directorio);

    /**
     * Actualiza el estado de un directorio.
     *
     * @param idDirectorio identificador del directorio
     * @param nuevoEstado nuevo estado asignado
     */
    void actualizarEstado(int idDirectorio, String nuevoEstado);

    /**
     * Obtiene todos los directorios registrados.
     *
     * @return lista de directorios
     */
    List<Directorio> obtenerTodos();

    /**
     * Elimina un directorio del sistema.
     *
     * @param idDirectorio identificador del directorio a eliminar
     */
    void eliminar(int idDirectorio);
}
