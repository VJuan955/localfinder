package org.example.dao.impl;

import org.example.database.DatabaseManager;
import org.example.dao.IndiceDAO;
import org.example.model.Indice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link IndiceDAO}.
 *
 * <p>Gestiona las entradas del índice invertido utilizadas
 * para recuperar documentos durante las búsquedas.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class IndiceDAOImpl implements IndiceDAO {

    private static final Logger logger = LoggerFactory.getLogger(IndiceDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertar(Indice indice) {
        logger.debug("Insertando entrada de índice para término {} y archivo {}", indice.getIdTermino(), indice.getIdArchivo());

        String sql = "INSERT INTO Indice (posiciones, snippet, frecuencia_documento, id_archivo, id_termino) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, indice.getPosiciones());
            pstmt.setString(2, indice.getSnippet());
            pstmt.setInt(3, indice.getFrecuenciaDocumento());
            pstmt.setInt(4, indice.getIdArchivo());
            pstmt.setInt(5, indice.getIdTermino());
            pstmt.executeUpdate();

            logger.info("Entrada de índice registrada");
        } catch (SQLException e) {
            logger.error("Error al insertar entrada de índice", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Indice> buscarPorTermino(int idTermino) {
        logger.debug("Buscando índices para término {}", idTermino);

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

                logger.debug("Se encontraron {} entradas para el término {}", mapeos.size(), idTermino);
            }
        } catch (SQLException e) {
            logger.error("Error al consultar índice para término {}", idTermino, e);
        }
        return mapeos;
    }
}
