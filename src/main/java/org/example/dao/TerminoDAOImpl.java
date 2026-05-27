package org.example.dao;

import org.example.model.Termino;
import java.sql.*;
import java.util.Optional;

public class TerminoDAOImpl implements TerminoDAO {

    @Override
    public int insertarOObtener(String palabra) {
        String buscarSql = "SELECT id_termino FROM Termino WHERE palabra = ?";
        String insertarSql = "INSERT INTO Termino (palabra, frecuencia_global) VALUES (?, 0)";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(buscarSql)) {
                pstmt.setString(1, palabra);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id_termino");
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(insertarSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, palabra);
                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error transaccional en término: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public Optional<Termino> buscarPorPalabra(String palabra) {
        String sql = "SELECT * FROM Termino WHERE palabra = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, palabra);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Termino term = new Termino();
                    term.setIdTermino(rs.getInt("id_termino"));
                    term.setPalabra(rs.getString("palabra"));
                    term.setFrecuenciaGlobal(rs.getInt("frecuencia_global"));
                    return Optional.of(term);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar término por palabra: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void incrementarFrecuenciaGlobal(int idTermino, int incremento) {
        String sql = "UPDATE Termino SET frecuencia_global = frecuencia_global + ? WHERE id_termino = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incremento);
            pstmt.setInt(2, idTermino);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar la frecuencia global del término: " + e.getMessage());
        }
    }
}
