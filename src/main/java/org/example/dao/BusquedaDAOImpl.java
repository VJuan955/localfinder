package org.example.dao;

import org.example.model.Busqueda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusquedaDAOImpl implements BusquedaDAO {

    @Override
    public void registrar(Busqueda busqueda) {
        String sql = "INSERT INTO Busqueda (filtros_aplicados, fecha_busqueda) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, busqueda.getFiltrosAplicados());
            pstmt.setLong(2, busqueda.getFechaBusqueda());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    busqueda.setIdBusqueda(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar búsqueda: " + e.getMessage());
        }
    }

    @Override
    public List<Busqueda> obtenerHistorial() {
        List<Busqueda> historial = new ArrayList<>();
        String sql = "SELECT * FROM Busqueda ORDER BY fecha_busqueda DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Busqueda bq = new Busqueda();
                bq.setIdBusqueda(rs.getInt("id_busqueda"));
                bq.setFiltrosAplicados(rs.getString("filtros_aplicados"));
                bq.setFechaBusqueda(rs.getLong("fecha_busqueda"));
                historial.add(bq);
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar historial: " + e.getMessage());
        }
        return historial;
    }
}
