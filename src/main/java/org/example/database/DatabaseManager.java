package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionar la conexión y la inicialización de la base de datos SQLite.
 *
 * <p>Esta clase proporciona un punto centralizado para obtener
 * conexiones JDBC y crear la estructura de tablas e índices
 * necesarios para el funcionamiento del sistema.</p>
 *
 * <p>La base de datos se almacena localmente en el archivo
 * {@code localfinder.db}.</p>
 *
 * @author VJuan955
 * @version 1.0
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    /**
     * URL JDBC utilizada para acceder a la base de datos SQLite.
     */
    private static final String URL = "jdbc:sqlite:localfinder.db";

    /**
     * Obtiene una nueva conexión a la base de datos.
     *
     * @return conexión activa a SQLite
     * @throws SQLException si ocurre un error al establecer la conexión
     */
    public static Connection getConnection() throws SQLException {
        logger.debug("Abriendo conexión SQLite");

        return DriverManager.getConnection(URL);
    }

    /**
     * Crea las tablas e índices requeridos por la aplicación
     * cuando aún no existen.
     *
     * <p>La operación es idempotente, por lo que puede ejecutarse
     * múltiples veces sin afectar la estructura existente.</p>
     */
    public static void inicializarBaseDeDatos() {
        logger.info("Inicializando base de datos");

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

            logger.info("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            logger.error("Error al inicializar la base de datos", e);
        }
    }
}
