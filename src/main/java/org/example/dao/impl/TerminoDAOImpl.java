package org.example.dao.impl;

import org.example.database.DatabaseManager;
import org.example.dao.TerminoDAO;
import org.example.model.Termino;
import java.sql.*;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link TerminoDAO}.
 *
 * <p>Gestiona los términos almacenados en el índice de búsqueda,
 * permitiendo su creación, consulta y actualización de frecuencia
 * global.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class TerminoDAOImpl implements TerminoDAO {

    private static final Logger logger = LoggerFactory.getLogger(TerminoDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertarOObtener(String palabra) {
        logger.debug("Buscando término: '{}'", palabra);

        String buscarSql = "SELECT id_termino FROM Termino WHERE palabra = ?";
        String insertarSql = "INSERT INTO Termino (palabra, frecuencia_global) VALUES (?, 0)";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
                pstmt.setString(1, palabra);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        logger.debug("Término '{}' encontrado", palabra);
                        return rs.getInt("id_termino");
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(insertarSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, palabra);
                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        logger.debug("Nuevo término registrado: '{}'", palabra);
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al insertar u obtener término '{}'", palabra, e);
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Termino> buscarPorPalabra(String palabra) {
        logger.debug("Consultando término '{}'", palabra);

        String sql = "SELECT * FROM Termino WHERE palabra = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, palabra);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Término '{}' localizado", palabra);

                    Termino term = new Termino();
                    term.setIdTermino(rs.getInt("id_termino"));
                    term.setPalabra(rs.getString("palabra"));
                    term.setFrecuenciaGlobal(rs.getInt("frecuencia_global"));
                    return Optional.of(term);
                }

                logger.debug("Término '{}' inexistente", palabra);
            }
        } catch (SQLException e) {
            logger.error("Error al buscar término '{}'", palabra, e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementarFrecuenciaGlobal(int idTermino, int incremento) {
        logger.debug("Incrementando frecuencia del término {} en {}", idTermino, incremento);

        String sql = "UPDATE Termino SET frecuencia_global = frecuencia_global + ? WHERE id_termino = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incremento);
            pstmt.setInt(2, idTermino);
            pstmt.executeUpdate();

            logger.debug("Frecuencia actualizada para término {}", idTermino);
        } catch (SQLException e) {
            logger.error("Error al actualizar frecuencia del término {}", idTermino, e);
        }
    }
}
