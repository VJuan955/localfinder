package org.example.model;

/**
 * Representa un archivo registrado e indexado por el sistema.
 *
 * <p>Almacena la información básica del archivo, incluyendo su ubicación,
 * tipo, tamaño, fecha de modificación y hash de verificación.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class Archivo {

    /** Identificador único del archivo. */
    private int idArchivo;

    /** Ruta absoluta del archivo en el sistema de archivos. */
    private String rutaArchivo;

    /** Nombre del archivo. */
    private String nombreArchivo;

    /** Tipo o extensión del archivo. */
    private String tipoArchivo;

    /** Tamaño del archivo en bytes. */
    private long tamano;

    /** Fecha de última modificación en formato de timestamp. */
    private long fechaModificacion;

    /** Hash utilizado para identificar cambios o duplicados. */
    private String hashArchivo;


    /**
     * Crea una instancia vacía de Archivo.
     */
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
