package org.example.dao.impl;

import org.example.database.DatabaseManager;
import org.example.dao.ResultadoDAO;
import org.example.model.Resultado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link ResultadoDAO}.
 *
 * <p>Gestiona el almacenamiento y recuperación de los resultados
 * generados por las búsquedas realizadas en el sistema.</p>
 *
 * <p>Las inserciones se realizan mediante procesamiento por lotes
 * para mejorar el rendimiento cuando se generan múltiples resultados
 * para una misma consulta.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class ResultadoDAOImpl implements ResultadoDAO {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void guardarResultados(List<Resultado> resultados) {
        logger.debug("Guardando lote de {} resultados", resultados.size());

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

                logger.info("Se almacenaron {} resultados", resultados.size());
            } catch (SQLException e) {
                conn.rollback();
                logger.warn("Rollback ejecutado durante el guardado de resultados", e);
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error al guardar resultados", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resultado> obtenerPorBusqueda(int idBusqueda) {
        logger.debug("Consultando resultados para búsqueda {}", idBusqueda);

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

            logger.debug("Recuperados {} resultados para búsqueda {}", resultados.size(), idBusqueda);
        } catch (SQLException e) {
            logger.error("Error al recuperar resultados para búsqueda {}", idBusqueda, e);
        }
        return resultados;
    }
}
