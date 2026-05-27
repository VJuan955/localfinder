package org.example.model;

public class Busqueda {
    private int idBusqueda;
    private String filtrosAplicados;
    private long fechaBusqueda;

    public Busqueda() {}

    public int getIdBusqueda() { return idBusqueda; }
    public void setIdBusqueda(int idBusqueda) { this.idBusqueda = idBusqueda; }

    public String getFiltrosAplicados() { return filtrosAplicados; }
    public void setFiltrosAplicados(String filtrosAplicados) { this.filtrosAplicados = filtrosAplicados; }

    public long getFechaBusqueda() { return fechaBusqueda; }
    public void setFechaBusqueda(long fechaBusqueda) { this.fechaBusqueda = fechaBusqueda; }
}
