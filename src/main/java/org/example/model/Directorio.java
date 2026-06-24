package org.example.model;

/**
 * Representa un directorio registrado para ser monitoreado o indexado.
 *
 * <p>Contiene información sobre su ubicación, estado y fecha de registro
 * en el sistema.</p>
 *
 * @author AbelardoQuinones00
 * @version 1.0
 */
public class Directorio {

    /** Identificador único de directorio. */
    private int idDirectorio;

    /** Ruta absoluta del directorio. */
    private String rutaDirectorio;

    /** Estado actual del directorio dentro del sistema. */
    private String estado;

    /** Fecha de registro en formato timestamp. */
    private long fechaRegistro;

    /**
     * Crea una instancia vacía de Directorio.
     */
    public Directorio() {}

    public int getIdDirectorio() { return idDirectorio; }
    public void setIdDirectorio(int idDirectorio) { this.idDirectorio = idDirectorio; }

    public String getRutaDirectorio() { return rutaDirectorio; }
    public void setRutaDirectorio(String rutaDirectorio) { this.rutaDirectorio = rutaDirectorio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public long getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(long fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
