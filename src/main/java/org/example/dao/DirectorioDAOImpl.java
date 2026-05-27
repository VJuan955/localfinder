package org.example.dao;

import org.example.model.Directorio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectorioDAOImpl implements DirectorioDAO {

    @Override
    public void insertar(Directorio directorio) {
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
        } catch (SQLException ex) {
            System.err.println("Error al insertar el directorio: " + ex);
        }
    }

    @Override
    public void actualizarEstado(int idDirectorio, String nuevoEstado) {
        String sql = "UPDATE Directorio SET estado = ? WHERE idDirectorio = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idDirectorio);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el directorio: " + e.getMessage());
        }
    }

    @Override
    public List<Directorio> obtenerTodos() {
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
        } catch (SQLException e) {
            System.err.println("Error al obtener el directorios: " + e.getMessage());
        }
        return directorios;
    }

    @Override
    public void eliminar(int idDirectorio) {
        String sql = "DELETE FROM Directorio WHERE id_directorio = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDirectorio);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar directorio: " + e.getMessage());
        }
    }
}
