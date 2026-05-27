package org.example.dao;

import org.example.model.Busqueda;
import java.util.List;

public interface BusquedaDAO {
    void registrar(Busqueda busqueda);
    List<Busqueda> obtenerHistorial();
}
