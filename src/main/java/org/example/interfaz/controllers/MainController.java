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
    
    public List<Busqueda> obtenerHistorial() {
        return busquedaService.obtenerHistorial();
    }
    
    public List<Directorio> obtenerDirectorios() {
        return directorioService.obtenerDirectorios();
    }
    
    public void agregarDirectorio(String ruta) {
        directorioService.agregarDirectorio(ruta);
    }
    
    public void eliminarDirectorio(int idDirectorio) {
        directorioService.eliminar(idDirectorio);
    }
}