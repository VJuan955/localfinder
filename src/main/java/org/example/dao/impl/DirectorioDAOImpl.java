package org.example.dao.impl;

import org.example.database.DatabaseManager;
import org.example.dao.DirectorioDAO;
import org.example.model.Directorio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link DirectorioDAO}.
 *
 * <p>Gestiona el almacenamiento y mantenimiento de los directorios
 * registrados para monitoreo e indexación.</p>
 *
 * @author michellsalazar
 * @version 1.0
 */
public class DirectorioDAOImpl implements DirectorioDAO {

    private static final Logger logger = LoggerFactory.getLogger(DirectorioDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertar(Directorio directorio) {
        logger.debug("Registrando directorio: {}", directorio.getRutaDirectorio());

        String sql = "INSERT INTO Directorio (ruta_directorio, estado, fecha_registro) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, directorio.getRutaDirectorio());
            pstmt.setString(2, directorio.getEstado());
            pstmt.setLong(3, directorio.getFechaRegistro());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    directorio.setIdDirectorio(generatedKeys.getInt(1));
                }
            }

            logger.info("Directorio registrado con ID {}", directorio.getIdDirectorio());
        } catch (SQLException e) {
            logger.error("Error al insertar directorio: {}", directorio.getRutaDirectorio(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizarEstado(int idDirectorio, String nuevoEstado) {
        logger.debug("Actualizando estado del directorio {} a {}", idDirectorio, nuevoEstado);

        String sql = "UPDATE Directorio SET estado = ? WHERE id_directorio = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idDirectorio);
            pstmt.executeUpdate();

            logger.info("Estado actualizado para directorio {}", idDirectorio);
        } catch (SQLException e) {
            logger.error("Error al actualizar directorio {}", idDirectorio, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Directorio> obtenerTodos() {
        logger.debug("Recuperando directorios registrados");

        List<Directorio> directorios = new ArrayList<>();
        String sql = "SELECT * FROM Directorio";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Directorio dir = new Directorio();
                dir.setIdDirectorio(rs.getInt("id_directorio"));
                dir.setRutaDirectorio(rs.getString("ruta_directorio"));
                dir.setEstado(rs.getString("estado"));
                dir.setFechaRegistro(rs.getLong("fecha_registro"));
                directorios.add(dir);
            }

            logger.debug("Se recuperaron {} directorios", directorios.size());
        } catch (SQLException e) {
            logger.error("Error al obtener directorios", e);
        }
        return directorios;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(int idDirectorio) {
        logger.debug("Eliminando directorio {}", idDirectorio);

        String sql = "DELETE FROM Directorio WHERE id_directorio = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDirectorio);
            pstmt.executeUpdate();

            logger.info("Directorio eliminado: {}", idDirectorio);
        } catch (SQLException e) {
            logger.error("Error al eliminar directorio {}", idDirectorio, e);
        }
    }
}
