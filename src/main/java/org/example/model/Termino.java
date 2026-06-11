package org.example.model;

/**
 * Representa un término almacenado en el índice de búsqueda.
 *
 * <p>Contiene la palabra indexada y la frecuencia total de aparición
 * en el conjunto de documentos.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class Termino {

    /** Identificador único del término */
    private int idTermino;

    /** Palabra indexada. */
    private String palabra;

    /** Frecuencia global del término en todos los documentos. */
    private int frecuenciaGlobal;

    /**
     * Crea una instancia vacía de Termino
     */
    public Termino() {}

    public int getIdTermino() { return idTermino; }
    public void setIdTermino(int idTermino) { this.idTermino = idTermino; }

    public String getPalabra() { return palabra; }
    public void setPalabra(String palabra) { this.palabra = palabra; }

    public int getFrecuenciaGlobal() { return frecuenciaGlobal; }
    public void setFrecuenciaGlobal(int frecuenciaGlobal) {  this.frecuenciaGlobal = frecuenciaGlobal; }
}
