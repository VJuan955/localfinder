package org.example.model;

public class Resultado {
    private int idResultado;
    private int posicionResultado;
    private double ranking;
    private int idBusqueda;
    private int idArchivo;

    public Resultado() {}

    public int getIdResultado() { return idResultado; }
    public void setIdResultado(int idResultado) { this.idResultado = idResultado; }

    public int getPosicionResultado() { return posicionResultado; }
    public void setPosicionResultado(int posicionResultado) { this.posicionResultado = posicionResultado; }

    public double getRanking() { return ranking; }
    public void setRanking(double ranking) { this.ranking = ranking; }

    public int getIdBusqueda() { return idBusqueda; }
    public void setIdBusqueda(int idBusqueda) { this.idBusqueda = idBusqueda; }

    public int getIdArchivo() { return idArchivo; }
    public void setIdArchivo(int idArchivo) { this.idArchivo = idArchivo; }
}
