package org.example.dao.impl;

import org.example.dao.ArchivoDAO;
import org.example.database.DatabaseManager;
import org.example.model.Archivo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC de {@link ArchivoDAO}.
 *
 * <p>Gestiona las operaciones de persistencia relacionadas con
 * los archivos registrados en el sistema utilizando SQLite.</p>
 *
 * @author michellsalazar
 * @version 1.0
 */
public class ArchivoDAOImpl implements ArchivoDAO {

    private static final Logger logger = LoggerFactory.getLogger(ArchivoDAOImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertar(Archivo archivo, int idDirectorio) {
        logger.debug("Insertando archivo: {}", archivo.getRutaArchivo());

        String sql = "INSERT INTO Archivo (ruta_archivo, nombre_archivo, tipo_archivo, tamano, hash_archivo, fecha_modificacion, id_directorio) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, archivo.getRutaArchivo());
            pstmt.setString(2, archivo.getNombreArchivo());
            pstmt.setString(3, archivo.getTipoArchivo());
            pstmt.setLong(4, archivo.getTamano());
            pstmt.setString(5, archivo.getHashArchivo());
            pstmt.setLong(6, archivo.getFechaModificacion());
            pstmt.setInt(7, idDirectorio);

            pstmt.executeUpdate();

            logger.info("Archivo registrado correctamente: {}", archivo.getNombreArchivo());
        } catch (SQLException e) {
            logger.error("Error al insertar archivo: {}", archivo.getRutaArchivo(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizar(Archivo archivo) {
        logger.debug("Actualizando archivo: {}", archivo.getRutaArchivo());

        String sql = "UPDATE Archivo SET nombre_archivo = ?, tipo_archivo = ?, tamano = ?," +
                "hash_archivo = ?, fecha_modificacion = ? WHERE ruta_archivo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, archivo.getNombreArchivo());
            pstmt.setString(2, archivo.getTipoArchivo());
            pstmt.setLong(3, archivo.getTamano());
            pstmt.setString(4, archivo.getHashArchivo());
            pstmt.setLong(5, archivo.getFechaModificacion());
            pstmt.setString(6, archivo.getRutaArchivo());

            pstmt.executeUpdate();

            logger.info("Archivo actualizado: {}", archivo.getRutaArchivo());
        } catch (SQLException e) {
            logger.error("Error al actualizar archivo: {}", archivo.getRutaArchivo(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Archivo> buscarPorRuta(String ruta) {
        logger.debug("Buscando archivo por rutas: {}", ruta);

        String sql = "SELECT * FROM Archivo WHERE ruta_archivo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ruta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Archivo encontrado: {}", ruta);

                    Archivo archivo = new Archivo();

                    archivo.setRutaArchivo(rs.getString("ruta_archivo"));
                    archivo.setNombreArchivo(rs.getString("nombre_archivo"));
                    archivo.setTipoArchivo(rs.getString("tipo_archivo"));
                    archivo.setTamano(rs.getLong("tamano"));
                    archivo.setHashArchivo(rs.getString("hash_archivo"));
                    archivo.setFechaModificacion(rs.getLong("fecha_modificacion"));

                    return Optional.of(archivo);
                }

                logger.debug("No se encontró archivo para la ruta: {}", ruta);
            }
        } catch (SQLException e) {
            logger.error("Error al buscar archivo: {}", ruta, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Archivo> obtenerTodos() {
        logger.debug("Recuperando listado de archivos");

        List<Archivo> archivos = new ArrayList<>();
        String sql = "SELECT * FROM Archivo";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Archivo doc = new Archivo();
                doc.setRutaArchivo(rs.getString("ruta_archivo"));
                doc.setNombreArchivo(rs.getString("nombre_archivo"));
                doc.setTipoArchivo(rs.getString("tipo_archivo"));
                doc.setTamano(rs.getLong("tamano"));
                doc.setFechaModificacion(rs.getLong("fecha_modificacion"));
                archivos.add(doc);
            }

            logger.debug("Se recuperaron {} archivos", archivos.size());
        } catch (SQLException e) {
            logger.error("Error al listar archivos", e);
        }
        return archivos;
    }
}
