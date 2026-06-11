package org.example.interfaz.controllers;

import org.example.model.Busqueda;
import org.example.model.Directorio;
import org.example.service.BusquedaService;
import org.example.service.DirectorioService;

import java.util.List;

public class MainController {

    private final BusquedaService busquedaService;
    private final DirectorioService directorioService;

    public MainController(DirectorioService directorioService, BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
        this.directorioService = directorioService;
    }

    // ─── Historial ───────────────────────────────────────────

    /**
     * Devuelve todas las búsquedas registradas en la BD,
     * ordenadas de más reciente a más antigua.
     */
    public List<Busqueda> obtenerHistorial() {
        return busquedaService.obtenerHistorial();
    }

    // ─── Configuración de directorios ────────────────────────

    /**
     * Devuelve todos los directorios registrados en la BD.
     */
    public List<Directorio> obtenerDirectorios() {
        return directorioService.obtenerDirectorios();
    }

    /**
     * Registra un nuevo directorio con estado 'activo'.
     */
    public void agregarDirectorio(String ruta) {
        directorioService.agregarDirectorio(ruta);
    }

    /**
     * Elimina un directorio de la BD por su ID.
     */
    public void eliminarDirectorio(int idDirectorio) {
        directorioService.eliminar(idDirectorio);
    }
}