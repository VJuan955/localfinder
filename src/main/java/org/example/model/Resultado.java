package org.example.model;

/**
 * Representa un resultado obtenido a partir de una búsqueda.
 *
 * <p>Contiene la posición y relevancia de un archivo dentro de
 * una consulta determinada.</p>
 *
 * @author AbelardoQuinones00
 * @version 1.0
 */
public class Resultado {

    /** Identificador único del resultado. */
    private int idResultado;

    /** Posición del resultado dentro del listado obtenido. */
    private int posicionResultado;

    /** Puntaje de relevancia calculado para el resultado. */
    private double ranking;

    /** Identificador de la búsqueda asociada. */
    private int idBusqueda;

    /** Identificador del archivo asociado. */
    private int idArchivo;

    /**
     * Crea una instancia vacía de Resultado.
     */
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
