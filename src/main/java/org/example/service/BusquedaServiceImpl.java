package org.example.service;

import org.example.dao.BusquedaDAO;
import org.example.model.Busqueda;

import java.util.List;

public class BusquedaServiceImpl implements BusquedaService {

    private final BusquedaDAO busquedaDAO;

    public BusquedaServiceImpl(BusquedaDAO busquedaDAO) {
        this.busquedaDAO = busquedaDAO;
    }

    @Override
    public List<Busqueda> obtenerHistorial() {
        return busquedaDAO.obtenerHistorial();
    }
}
