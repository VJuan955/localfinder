package org.example.dao;

import org.example.model.Resultado;
import java.util.List;

public interface ResultadoDAO {
    void guardarResultados(List<Resultado> resultados);
    List<Resultado> obtenerPorBusqueda(int idBusqueda);
}
