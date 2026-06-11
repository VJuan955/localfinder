package org.example.dao;

import org.example.model.Archivo;
import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de persistencia para la entidad {@link Archivo}.
 *
 * <p>Permite registrar, actualizar y consultar archivos almacenados
 * en la base de datos.</p>
 *
 * @author michellsalazar
 * @version 1.0
 */
public interface ArchivoDAO {

    /**
     * Inserta un nuevo archivo asociado a un directorio.
     *
     * @param archivo información del archivo a registrar
     * @param idDirectorio identificador del directorio propietario
     */
    void insertar(Archivo archivo, int idDirectorio);

    /**
     * Actualiza la información de un archivo existente.
     *
     * @param archivo archivo con los datos actualizados
     */
    void actualizar(Archivo archivo);

    /**
     * Busca un archivo a partir de su ruta absoluta.
     *
     * @param ruta ruta completa del archivo
     * @return un {@code Optional} con el archivo encontrado,
     *         o vacío si no existe
     */
    Optional<Archivo> buscarPorRuta(String ruta);

    /**
     * Obtiene todos los archivos registrados.
     *
     * @return lista de archivos almacenados
     */
    List<Archivo> obtenerTodos();
}
