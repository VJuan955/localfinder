package org.example.model;

public class DirectorioVista {

    private final Directorio directorio;
    private final int archivosIndexados;

    public DirectorioVista(Directorio directorio, int archivosIndexados) {
        this.directorio = directorio;
        this.archivosIndexados = archivosIndexados;
    }

    public Directorio getDirectorio() { return directorio; }
    public int getArchivosIndexados() { return archivosIndexados; }

    public String getTextoLista() {
        return directorio.getRutaDirectorio()
                + "  [" + directorio.getEstado() + "]"
                + " — " + archivosIndexados + " archivo(s) indexado(s)";
    }
}
