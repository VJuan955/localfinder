package org.example.model;

/**
 * Representa una búsqueda realizada por el usuario.
 *
 * <p>Almacena los filtros utilizados y la fecha en la que se ejecutó
 * la consulta.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class Busqueda {

    /** Identificador único de la búsqueda */
    private int idBusqueda;

    /** Filtros aplicados durante la búsqueda */
    private String filtrosAplicados;

    /** Fecha de ejecución de la búsqueda en formato timestamp. */
    private long fechaBusqueda;

    /**
     * Crea una instancia vacía de Busqueda.
     */
    public Busqueda() {}

    public int getIdBusqueda() { return idBusqueda; }
    public void setIdBusqueda(int idBusqueda) { this.idBusqueda = idBusqueda; }

    public String getFiltrosAplicados() { return filtrosAplicados; }
    public void setFiltrosAplicados(String filtrosAplicados) { this.filtrosAplicados = filtrosAplicados; }

    public long getFechaBusqueda() { return fechaBusqueda; }
    public void setFechaBusqueda(long fechaBusqueda) { this.fechaBusqueda = fechaBusqueda; }
}
