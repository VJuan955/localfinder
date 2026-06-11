package org.example.service;

import org.example.dao.DirectorioDAO;
import org.example.model.Directorio;

import java.util.List;

public class DirectorioServiceImpl implements DirectorioService {

    private final DirectorioDAO directorioDAO;

    public DirectorioServiceImpl(DirectorioDAO directorioDAO) {
        this.directorioDAO = directorioDAO;
    }

    @Override
    public List<Directorio> obtenerDirectorios() {
        return directorioDAO.obtenerTodos();
    }

    @Override
    public void agregarDirectorio(String ruta) {
        Directorio dir = new Directorio();
        dir.setRutaDirectorio(ruta);
        dir.setEstado("activo");
        dir.setFechaRegistro(System.currentTimeMillis());
        directorioDAO.insertar(dir);
    }

    @Override
    public void eliminarDirectorio(int idDirectorio) {
        directorioDAO.eliminar(idDirectorio);
    }

    @Override
    public void eliminar(int idDirectorio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminar'");
    }
}
