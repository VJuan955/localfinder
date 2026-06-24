package org.example.interfaz.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.interfaz.controllers.MainController;
import org.example.model.Busqueda;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialView extends VBox {

    private final MainController controller;

    // Formateador de fecha legible
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                             .withZone(ZoneId.systemDefault());

    @SuppressWarnings({ "deprecation", "unchecked" })
public HistorialView(MainController controller) {

        this.controller = controller;

        setSpacing(15);
        setPadding(new Insets(30));

        
        Label titulo = new Label("🕒 Historial de Búsquedas");
        titulo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#0f172a;"
        );

        Label subtitulo = new Label(
                "Consultas realizadas y registradas en la base de datos"
        );
        subtitulo.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");

        
        Button btnLimpiar = new Button("🗑 Limpiar vista");
        btnLimpiar.setStyle(
                "-fx-background-color:#fee2e2;" +
                "-fx-text-fill:#b91c1c;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox cabecera = new HBox(titulo, spacer, btnLimpiar);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        
        TableView<Busqueda> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Busqueda, String> colTermino =
                new TableColumn<>("Término buscado");
        colTermino.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFiltrosAplicados() != null
                        ? data.getValue().getFiltrosAplicados()
                        : "(sin término)"
                )
        );

        TableColumn<Busqueda, String> colFecha =
                new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> {
            long ms = data.getValue().getFechaBusqueda();
            String fechaLegible = FORMATO.format(Instant.ofEpochMilli(ms));
            return new javafx.beans.property.SimpleStringProperty(fechaLegible);
        });

        TableColumn<Busqueda, String> colId =
                new TableColumn<>("ID");
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue().getIdBusqueda())
                )
        );
        colId.setMaxWidth(70);

        tabla.getColumns().addAll(colId, colTermino, colFecha);

        
        cargarHistorial(tabla);

        
        Label lblEstado = new Label();
        lblEstado.setStyle("-fx-text-fill:#64748b; -fx-font-size:12px;");

        int total = tabla.getItems().size();
        if (total == 0) {
            lblEstado.setText("No hay búsquedas registradas aún.");
        } else {
            lblEstado.setText("Mostrando " + total + " búsqueda(s) registrada(s).");
        }

        
        btnLimpiar.setOnAction(e -> {
            tabla.getItems().clear();
            lblEstado.setText("Vista limpiada. Los registros siguen en la base de datos.");
        });

        getChildren().addAll(cabecera, subtitulo, tabla, lblEstado);
    }

    private void cargarHistorial(TableView<Busqueda> tabla) {
        try {
            List<Busqueda> historial = controller.obtenerHistorial();
            tabla.getItems().setAll(historial);
        } catch (Exception e) {
            tabla.setPlaceholder(
                new Label("⚠ No se pudo conectar con la base de datos: "
                          + e.getMessage())
            );
        }
    }
}