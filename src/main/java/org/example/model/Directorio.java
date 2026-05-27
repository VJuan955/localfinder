package org.example.model;

public class Directorio {
    private int idDirectorio;
    private String rutaDirectorio;
    private String estado;
    private long fechaRegistro;

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
