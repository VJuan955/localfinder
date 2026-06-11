package org.example.service;

import org.example.model.Directorio;

import java.util.List;

public interface DirectorioService {
    List<Directorio> obtenerDirectorios();
    void agregarDirectorio(String ruta);
    void eliminarDirectorio(int idDirectorio);
    void eliminar(int idDirectorio);
}
