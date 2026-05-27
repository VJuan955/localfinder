package org.example.model;

public class Termino {
    private int idTermino;
    private String palabra;
    private int frecuenciaGlobal;

    public Termino() {}

    public int getIdTermino() { return idTermino; }
    public void setIdTermino(int idTermino) { this.idTermino = idTermino; }

    public String getPalabra() { return palabra; }
    public void setPalabra(String palabra) { this.palabra = palabra; }

    public int getFrecuenciaGlobal() { return frecuenciaGlobal; }
    public void setFrecuenciaGlobal(int frecuenciaGlobal) {  this.frecuenciaGlobal = frecuenciaGlobal; }
}
