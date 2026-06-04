package org.example.model;

public class Archivo {
    private int idArchivo;
    private String rutaArchivo;
    private String nombreArchivo;
    private String tipoArchivo;
    private long tamano;
    private long fechaModificacion;
    private String hashArchivo;

    public Archivo() {}

    public int getIdArchivo() { return idArchivo; }
    public void setIdArchivo(int idArchivo) { this.idArchivo = idArchivo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }

    public long getTamano() { return tamano; }
    public void setTamano(long tamano) { this.tamano = tamano; }

    public long getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(long fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    public String getHashArchivo() { return hashArchivo; }
    public void setHashArchivo(String hashArchivo) { this.hashArchivo = hashArchivo; }
}
