package org.example.dao;

import org.example.model.Archivo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchivoDAOImpl implements ArchivoDAO {

    @Override
    public void insertar(Archivo archivo, int idDirectorio) {
        String sql = "INSERT INTO Archivo (ruta_directorio, nombre_archivo, tipo_archivo, tamano, hash_archivo, fecha_modificacion, id_directorio) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
        } catch (SQLException e) {
            System.err.println("Error al insertar archivo: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Archivo archivo) {
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
        } catch (SQLException e) {
            System.err.println("Error al actualizar archivo: " + e.getMessage());
        }
    }

    @Override
    public Optional<Archivo> buscarPorRuta(String ruta) {
        String sql = "SELECT * FROM Archivo WHERE ruta_archivo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ruta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Archivo archivo = new Archivo();

                    archivo.setRutaArchivo(rs.getString("ruta_archivo"));
                    archivo.setNombreArchivo(rs.getString("nombre_archivo"));
                    archivo.setTipoArchivo(rs.getString("tipo_archivo"));
                    archivo.setTamano(rs.getLong("tamano"));
                    archivo.setHashArchivo(rs.getString("hash_archivo"));
                    archivo.setFechaModificacion(rs.getLong("fecha_modificacion"));

                    return Optional.of(archivo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar archivo: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<Archivo> obtenerTodos() {
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
        } catch (SQLException e) {
            System.err.println("Error al listar archivos: " + e.getMessage());
        }
        return archivos;
    }
}
