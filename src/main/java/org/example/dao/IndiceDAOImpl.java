package org.example.dao;

import org.example.model.Indice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IndiceDAOImpl implements IndiceDAO {
    @Override
    public void insertar(Indice indice) {
        String sql = "INSERT INTO Indice (posiciones, snippet, frecuencia_documento, id_archivo, id_termino) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, indice.getPosiciones());
            pstmt.setString(2, indice.getSnippet());
            pstmt.setInt(3, indice.getFrecuenciaDocumento());
            pstmt.setInt(4, indice.getIdArchivo());
            pstmt.setInt(5, indice.getIdTermino());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar entrada en el índice: " + e.getMessage());
        }
    }

    @Override
    public List<Indice> buscarPorTermino(int idTermino) {
        List<Indice> mapeos = new ArrayList<>();
        String sql = "SELECT * FROM Indice WHERE id_termino = ? ORDER BY frecuencia_documento DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTermino);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Indice ind = new Indice();
                    ind.setIdIndice(rs.getInt("id_indice"));
                    ind.setPosiciones(rs.getString("posiciones"));
                    ind.setSnippet(rs.getString("snippet"));
                    ind.setFrecuenciaDocumento(rs.getInt("frecuencia_documento"));
                    ind.setIdArchivo(rs.getInt("id_archivo"));
                    ind.setIdTermino(rs.getInt("id_termino"));
                    mapeos.add(ind);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en la consulta por término indexado: " + e.getMessage());
        }
        return mapeos;
    }
}
