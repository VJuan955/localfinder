package org.example.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Path DATA_DIR = Paths.get(
            System.getProperty("user.home"), ".localfinder");
    private static final Path DB_FILE = DATA_DIR.resolve("localfinder.db");
    private static final String URL = "jdbc:sqlite:" + DB_FILE.toAbsolutePath();

    static {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de datos de LocalFinder", e);
        }
    }

    public static Path getDataDir() {
        return DATA_DIR;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBaseDeDatos() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS Directorio (" +
                    "id_directorio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ruta_directorio TEXT NOT NULL, " +
                    "estado TEXT NOT NULL, " +
                    "fecha_registro INTEGER NOT NULL)");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_directorio_ruta ON Directorio(ruta_directorio)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Archivo (" +
                    "id_archivo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ruta_archivo TEXT NOT NULL, " +
                    "nombre_archivo TEXT NOT NULL, " +
                    "tipo_archivo TEXT NOT NULL, " +
                    "tamano INTEGER NOT NULL, " +
                    "hash_archivo TEXT, " +
                    "fecha_modificacion INTEGER NOT NULL, " +
                    "id_directorio INTEGER, " +
                    "FOREIGN KEY(id_directorio) REFERENCES Directorio(id_directorio))");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_archivo_hash ON Archivo(hash_archivo)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_archivo_fecha ON Archivo(fecha_modificacion)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Termino (" +
                    "id_termino INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "palabra TEXT NOT NULL UNIQUE, " +
                    "frecuencia_global INTEGER NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Indice (" +
                    "id_indice INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "posiciones TEXT NOT NULL," +
                    "snippet TEXT, " +
                    "frecuencia_documento INTEGER NOT NULL," +
                    "id_archivo INTEGER NOT NULL, " +
                    "id_termino INTEGER NOT NULL, " +
                    "FOREIGN KEY(id_archivo) REFERENCES Archivo(id_archivo), " +
                    "FOREIGN KEY(id_termino) REFERENCES Termino(id_termino))");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_indice_term_freq ON Indice(id_termino, frecuencia_documento DESC)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Busqueda (" +
                    "id_busqueda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "filtros_aplicados TEXT, " +
                    "fecha_busqueda INTEGER NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Busqueda_Termino (" +
                    "id_busqueda INTEGER NOT NULL, " +
                    "id_termino INTEGER NOT NULL, " +
                    "PRIMARY KEY(id_busqueda, id_termino), " +
                    "FOREIGN KEY(id_busqueda) REFERENCES Busqueda(id_busqueda), " +
                    "FOREIGN KEY(id_termino) REFERENCES Termino(id_termino))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Resultado (" +
                    "id_resultado INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "posicion_resultado INTEGER NOT NULL, " +
                    "ranking REAL NOT NULL, " +
                    "id_busqueda INTEGER NOT NULL, " +
                    "id_archivo INTEGER NOT NULL, " +
                    "FOREIGN KEY(id_busqueda) REFERENCES Busqueda(id_busqueda), " +
                    "FOREIGN KEY(id_archivo) REFERENCES Archivo(id_archivo))");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_resultado_historial ON Resultado(id_busqueda, posicion_resultado)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_resultado_ranking ON Resultado(ranking)");

        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos: " + e.getMessage(), e);
        }
    }
}
