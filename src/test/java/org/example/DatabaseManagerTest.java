package org.example;

import org.example.dao.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseManagerTest {

    private static final String[] TABLAS_ESPERADAS = {
            "Directorio",
            "Archivo",
            "Termino",
            "Indice",
            "Busqueda",
            "Busqueda_Termino",
            "Resultado"
    };

    @BeforeEach
    void setUp() {
        DatabaseManager.inicializarBaseDeDatos();
    }

    @Test
    @DisplayName("Debe obtener una conexión válida (no nula y no cerrada)")
    void debeObtenerConexionValida() throws SQLException {
        try (Connection conexion = DatabaseManager.getConnection()) {
            assertNotNull(conexion, "La conexión no debe ser nula");
            assertFalse(conexion.isClosed(), "La conexión no debe estar cerrada");
        }
    }

    @Test
    @DisplayName("Después de inicializarBaseDeDatos(), las 7 tablas deben existir")
    void debenExistirLasSieteTablas() throws SQLException {
        try (Connection conexion = DatabaseManager.getConnection()) {
            for (String tabla : TABLAS_ESPERADAS) {
                assertTrue(
                        existeTabla(conexion, tabla),
                        "La tabla '" + tabla + "' debe existir en la base de datos"
                );
            }
        }
    }

    @Test
    @DisplayName("Debe verificar cada tabla con sqlite_master")
    void debeVerificarTablasConSqliteMaster() throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (Connection conexion = DatabaseManager.getConnection();
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            for (String tabla : TABLAS_ESPERADAS) {
                pstmt.setString(1, tabla);

                try (ResultSet rs = pstmt.executeQuery()) {
                    assertTrue(
                            rs.next(),
                            "sqlite_master debe contener la tabla: " + tabla
                    );
                    assertEquals(tabla, rs.getString("name"), "El nombre de la tabla debe coincidir");
                }
            }
        }
    }

    private boolean existeTabla(Connection conexion, String nombreTabla) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nombreTabla);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
