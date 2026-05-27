package org.example.model;

public class Indice {
    private int idIndice;
    private String posiciones;
    private String snippet;
    private int frecuenciaDocumento;
    private int idArchivo;
    private int idTermino;

    public Indice() {}

    public int getIdIndice() { return idIndice; }
    public void setIdIndice(int idIndice) { this.idIndice = idIndice; }

    public String getPosiciones() { return posiciones; }
    public void setPosiciones(String posiciones) { this.posiciones = posiciones; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }

    public int getFrecuenciaDocumento() { return frecuenciaDocumento; }
    public void setFrecuenciaDocumento(int frecuenciaDocumento) { this.frecuenciaDocumento = frecuenciaDocumento; }

    public int getIdArchivo() { return idArchivo; }
    public void setIdArchivo(int idArchivo) { this.idArchivo = idArchivo; }

    public int getIdTermino() { return idTermino; }
    public void setIdTermino(int idTermino) { this.idTermino = idTermino; }
}
