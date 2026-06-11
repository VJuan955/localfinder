package org.example.dao.impl;

import org.example.dao.BusquedaDAO;
import org.example.database.DatabaseManager;
import org.example.model.Busqueda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link BusquedaDAO}.
 *
 * <p>Permite registrar búsquedas realizadas por el usuario
 * y recuperar el historial almacenado.</p>
 *
 * @author michellsalazar
 * @version 1.0
 */
public class BusquedaDAOImpl implements BusquedaDAO {

    private static final Logger logger = LoggerFactory.getLogger(BusquedaDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void registrar(Busqueda busqueda) {
        logger.debug("Registrando búsqueda con filtros: {}", busqueda.getFiltrosAplicados());

        String sql = "INSERT INTO Busqueda (filtros_aplicados, fecha_busqueda) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, busqueda.getFiltrosAplicados());
            pstmt.setLong(2, busqueda.getFechaBusqueda());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    busqueda.setIdBusqueda(generatedKeys.getInt(1));

                    logger.info("Búsqueda registrada con ID {}", busqueda.getIdBusqueda());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al registrar búsqueda", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Busqueda> obtenerHistorial() {
        logger.debug("Recuperando historial de búsquedas");

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

            logger.debug("Historial recuperado: {} búsquedas", historial.size());
        } catch (SQLException e) {
            logger.error("Error al recuperar historial", e);
        }
        return historial;
    }
}
