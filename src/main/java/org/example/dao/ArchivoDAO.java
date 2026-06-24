package org.example.dao;

import org.example.model.Archivo;
import java.util.List;
import java.util.Optional;

public interface ArchivoDAO {
    void insertar(Archivo archivo, int idDirectorio);
    void actualizar(Archivo archivo);
    Optional<Archivo> buscarPorRuta(String ruta);
    Optional<Archivo> buscarPorId(int idArchivo);
    List<Archivo> obtenerTodos();
    int contar();
    int contarPorDirectorio(int idDirectorio);
    long obtenerUltimaModificacion();
}
