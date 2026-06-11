package org.example.interfaz.controllers;

import org.example.dao.BusquedaDAO;
import org.example.dao.DirectorioDAO;
import org.example.dao.impl.BusquedaDAOImpl;
import org.example.dao.impl.DirectorioDAOImpl;
import org.example.model.Busqueda;
import org.example.model.Directorio;

import java.util.List;

public class MainController {

    // ─── DAOs ────────────────────────────────────────────────
    private final BusquedaDAO busquedaDAO;
    private final DirectorioDAO directorioDAO;

    public MainController() {
        this.busquedaDAO   = new BusquedaDAOImpl();
        this.directorioDAO = new DirectorioDAOImpl();
    }

    // ─── Historial ───────────────────────────────────────────

    /**
     * Devuelve todas las búsquedas registradas en la BD,
     * ordenadas de más reciente a más antigua.
     */
    public List<Busqueda> obtenerHistorial() {
        return busquedaDAO.obtenerHistorial();
    }

    // ─── Configuración de directorios ────────────────────────

    /**
     * Devuelve todos los directorios registrados en la BD.
     */
    public List<Directorio> obtenerDirectorios() {
        return directorioDAO.obtenerTodos();
    }

    /**
     * Registra un nuevo directorio con estado 'activo'.
     */
    public void agregarDirectorio(String ruta) {
        Directorio dir = new Directorio();
        dir.setRutaDirectorio(ruta);
        dir.setEstado("activo");
        dir.setFechaRegistro(System.currentTimeMillis());
        directorioDAO.insertar(dir);
    }

    /**
     * Elimina un directorio de la BD por su ID.
     */
    public void eliminarDirectorio(int idDirectorio) {
        directorioDAO.eliminar(idDirectorio);
    }
}
