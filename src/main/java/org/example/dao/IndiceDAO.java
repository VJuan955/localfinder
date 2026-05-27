package org.example.dao;

import org.example.model.Indice;
import java.util.List;

public interface IndiceDAO {
    void insertar(Indice indice);
    List<Indice> buscarPorTermino(int idTermino);
}
