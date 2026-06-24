package org.example.model;

public class CoincidenciaIndice {

    private final int idArchivo;
    private final float score;

    public CoincidenciaIndice(int idArchivo, float score) {
        this.idArchivo = idArchivo;
        this.score = score;
    }

    public int getIdArchivo() { return idArchivo; }
    public float getScore() { return score; }
}
