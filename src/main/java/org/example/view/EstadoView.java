package org.example.interfaz.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.interfaz.controllers.MainController;
import org.example.model.EstadisticasSistema;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EstadoView extends VBox {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.systemDefault());

    public EstadoView(MainController controller) {

        setSpacing(15);
        setPadding(new Insets(30));

        Label titulo = new Label("📊 Estado del Sistema");
        titulo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#0f172a;"
        );

        Label bd = new Label("Base de Datos: Activa");
        bd.setStyle("-fx-text-fill:#15803d; -fx-font-weight:bold;");

        try {
            EstadisticasSistema stats = controller.obtenerEstadisticas();

            Label docs = new Label("Documentos indexados: " + stats.getDocumentosIndexados());
            Label dirs = new Label(
                    "Directorios: " + stats.getDirectoriosActivos()
                            + " activos / " + stats.getDirectoriosTotales() + " totales"
            );

            String ultimaTexto = stats.getUltimaModificacionArchivo() > 0
                    ? FORMATO.format(Instant.ofEpochMilli(stats.getUltimaModificacionArchivo()))
                    : "Sin archivos indexados";

            Label ultima = new Label("Última modificación indexada: " + ultimaTexto);

            String estadoIndice = stats.getDocumentosIndexados() > 0
                    ? "Índice Lucene: activo"
                    : "Índice Lucene: vacío (sincroniza en Configuración)";

            Label indice = new Label(estadoIndice);
            indice.setStyle(stats.getDocumentosIndexados() > 0
                    ? "-fx-text-fill:#15803d; -fx-font-weight:bold;"
                    : "-fx-text-fill:#b45309;");

            docs.setStyle("-fx-text-fill:#334155;");
            dirs.setStyle("-fx-text-fill:#334155;");
            ultima.setStyle("-fx-text-fill:#64748b;");

            getChildren().addAll(titulo, bd, indice, docs, dirs, ultima);
        } catch (Exception e) {
            Label error = new Label("⚠ No se pudieron cargar las estadísticas: " + e.getMessage());
            error.setStyle("-fx-text-fill:#b91c1c;");
            getChildren().addAll(titulo, bd, error);
        }
    }
}
