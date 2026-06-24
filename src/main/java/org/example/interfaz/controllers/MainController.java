package org.example.interfaz.controllers;

import org.example.model.Archivo;
import org.example.model.Busqueda;
import org.example.model.DirectorioVista;
import org.example.model.EstadisticasSistema;
import org.example.service.BusquedaService;
import org.example.service.DirectorioService;
import org.example.service.SistemaBusquedaService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MainController {

    private final BusquedaService busquedaService;
    private final DirectorioService directorioService;
    private final SistemaBusquedaService sistemaBusquedaService;

    public MainController(DirectorioService directorioService,
                          BusquedaService busquedaService,
                          SistemaBusquedaService sistemaBusquedaService) {
        this.busquedaService = busquedaService;
        this.directorioService = directorioService;
        this.sistemaBusquedaService = sistemaBusquedaService;
    }

    public List<Busqueda> obtenerHistorial() {
        return busquedaService.obtenerHistorial();
    }

    public List<DirectorioVista> obtenerDirectoriosIndexados() {
        return sistemaBusquedaService.obtenerDirectoriosIndexados();
    }

    public void agregarDirectorio(String ruta) throws IOException {
        Path path = Path.of(ruta.trim());
        if (!Files.isDirectory(path)) {
            throw new IOException("La ruta no existe o no es una carpeta válida: " + ruta);
        }
        directorioService.agregarDirectorio(path.toAbsolutePath().toString());
    }

    public void eliminarDirectorio(int idDirectorio) {
        directorioService.eliminarDirectorio(idDirectorio);
    }

    public List<Archivo> buscar(String consulta, String tipoArchivo) {
        return sistemaBusquedaService.realizarBusqueda(consulta, tipoArchivo);
    }

    public void sincronizarIndice() {
        sistemaBusquedaService.sincronizarIndice();
    }

    public EstadisticasSistema obtenerEstadisticas() {
        return sistemaBusquedaService.obtenerEstadisticas();
    }

    public void abrirArchivo(String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new IOException("El archivo ya no existe en disco: " + rutaArchivo);
        }
        if (!Desktop.isDesktopSupported()) {
            throw new IOException("Este sistema no permite abrir archivos desde la aplicación.");
        }
        Desktop.getDesktop().open(archivo);
    }
}
