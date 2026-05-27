package org.example.dao;

import org.example.model.Directorio;
import java.util.List;

public interface DirectorioDAO {
    void insertar(Directorio directorio);
    void actualizarEstado(int idDirectorio, String nuevoEstado);
    List<Directorio> obtenerTodos();
    void eliminar(int idDirectorio);
}
