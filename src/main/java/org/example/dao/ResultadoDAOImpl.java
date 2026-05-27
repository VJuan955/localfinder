package org.example.dao;

import org.example.model.Resultado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultadoDAOImpl implements ResultadoDAO {
    @Override
    public void guardarResultados(List<Resultado> resultados) {
        String sql = "INSERT INTO Resultado (posicion_resultado, ranking, id_busqueda, id_archivo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Activamos procesamiento por lotes para alta eficiencia
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Resultado res : resultados) {
                    pstmt.setInt(1, res.getPosicionResultado());
                    pstmt.setDouble(2, res.getRanking());
                    pstmt.setInt(3, res.getIdBusqueda());
                    pstmt.setInt(4, res.getIdArchivo());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar lote de resultados: " + e.getMessage());
        }
    }

    @Override
    public List<Resultado> obtenerPorBusqueda(int idBusqueda) {
        List<Resultado> resultados = new ArrayList<>();
        String sql = "SELECT * FROM Resultado WHERE id_busqueda = ? ORDER BY posicion_resultado ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idBusqueda);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resultado res = new Resultado();
                    res.setIdResultado(rs.getInt("id_resultado"));
                    res.setPosicionResultado(rs.getInt("posicion_resultado"));
                    res.setRanking(rs.getDouble("ranking"));
                    res.setIdBusqueda(rs.getInt("id_busqueda"));
                    res.setIdArchivo(rs.getInt("id_archivo"));
                    resultados.add(res);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar resultados históricos: " + e.getMessage());
        }
        return resultados;
    }
}
