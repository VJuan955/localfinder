package org.example.model;

public class EstadisticasSistema {

    private final int documentosIndexados;
    private final int directoriosActivos;
    private final int directoriosTotales;
    private final long ultimaModificacionArchivo;

    public EstadisticasSistema(int documentosIndexados, int directoriosActivos,
                               int directoriosTotales, long ultimaModificacionArchivo) {
        this.documentosIndexados = documentosIndexados;
        this.directoriosActivos = directoriosActivos;
        this.directoriosTotales = directoriosTotales;
        this.ultimaModificacionArchivo = ultimaModificacionArchivo;
    }

    public int getDocumentosIndexados() { return documentosIndexados; }
    public int getDirectoriosActivos() { return directoriosActivos; }
    public int getDirectoriosTotales() { return directoriosTotales; }
    public long getUltimaModificacionArchivo() { return ultimaModificacionArchivo; }
}
