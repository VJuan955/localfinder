package org.example.model;

/**
 * Representa una entrada del índice de búsqueda.
 *
 * <p>Relaciona un término con un archivo específico, almacenando
 * información utilizada durante el proceso de recuperación de
 * documentos.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class Indice {

    /** Identificacor único de la entrada del índice */
    private int idIndice;

    /** Posiciones donde aparece el término dentro del documento. */
    private String posiciones;

    /** Fragmento de texto asociado al término indexado. */
    private String snippet;

    /** Frecuencia de aparación del término en el documento. */
    private int frecuenciaDocumento;

    /** Identificador del archivo relacionado. */
    private int idArchivo;

    /** Identificador del término relacionado. */
    private int idTermino;

    /**
     * Crea una instancia vacía de Indice.
     */
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
