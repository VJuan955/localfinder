package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.interfaz.controllers.MainController;
import org.example.model.Directorio;

import java.util.List;

public class ConfiguracionView extends VBox {

    private final MainController controller;
    private final ListView<String> listaRutas;

    // Guardamos los IDs para poder eliminar correctamente
    private final javafx.collections.ObservableList<Directorio> directorios =
            javafx.collections.FXCollections.observableArrayList();

    public ConfiguracionView(MainController controller) {

        this.controller  = controller;
        this.listaRutas  = new ListView<>();

        setSpacing(15);
        setPadding(new Insets(30));

        // ─── Título ───────────────────────────────────────────
        Label titulo = new Label("⚙ Configuración del Índice");
        titulo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#0f172a;"
        );

        Label subtitulo = new Label(
                "Gestiona las carpetas que LocalFinder rastreará e indexará"
        );
        subtitulo.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");

        // ─── Lista de rutas ───────────────────────────────────
        listaRutas.setPrefHeight(200);
        VBox.setVgrow(listaRutas, Priority.ALWAYS);

        cargarDirectorios();

        // ─── Campo para nueva ruta ────────────────────────────
        TextField txtNuevaRuta = new TextField();
        txtNuevaRuta.setPromptText("Ej: C:/Documentos/Proyectos");
        txtNuevaRuta.setStyle(
                "-fx-padding:10;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );
        HBox.setHgrow(txtNuevaRuta, Priority.ALWAYS);

        // ─── Botones ──────────────────────────────────────────
        Button btnAgregar = new Button("➕ Agregar");
        btnAgregar.setStyle(
                "-fx-background-color:#1e40af;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:10 18;"
        );

        Button btnEliminar = new Button("🗑 Eliminar seleccionado");
        btnEliminar.setStyle(
                "-fx-background-color:#fee2e2;" +
                "-fx-text-fill:#b91c1c;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:10 18;"
        );

        HBox panelInput = new HBox(10, txtNuevaRuta, btnAgregar);
        panelInput.setAlignment(Pos.CENTER_LEFT);

        // ─── Etiqueta de estado ───────────────────────────────
        Label lblEstado = new Label("");
        lblEstado.setStyle("-fx-font-size:12px;");

        // ─── Acción AGREGAR ───────────────────────────────────
        btnAgregar.setOnAction(e -> {
            String ruta = txtNuevaRuta.getText().trim();

            if (ruta.isEmpty()) {
                mostrarEstado(lblEstado,
                        "⚠ Escribe una ruta antes de agregar.", "#b45309");
                return;
            }

            // Verificar que no esté duplicada
            boolean duplicada = directorios.stream()
                    .anyMatch(d -> d.getRutaDirectorio().equalsIgnoreCase(ruta));

            if (duplicada) {
                mostrarEstado(lblEstado,
                        "⚠ Esa ruta ya está en la lista.", "#b45309");
                return;
            }

            try {
                controller.agregarDirectorio(ruta);
                txtNuevaRuta.clear();
                cargarDirectorios();   // recarga la lista desde la BD
                mostrarEstado(lblEstado,
                        "✔ Directorio agregado correctamente.", "#15803d");
            } catch (Exception ex) {
                mostrarEstado(lblEstado,
                        "✘ Error al agregar: " + ex.getMessage(), "#b91c1c");
            }
        });

        // ─── Acción ELIMINAR ──────────────────────────────────
        btnEliminar.setOnAction(e -> {
            int idx = listaRutas.getSelectionModel().getSelectedIndex();

            if (idx < 0) {
                mostrarEstado(lblEstado,
                        "⚠ Selecciona una ruta de la lista primero.", "#b45309");
                return;
            }

            Directorio seleccionado = directorios.get(idx);

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar \"" + seleccionado.getRutaDirectorio() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText("Confirmar eliminación");

            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.YES) {
                    try {
                        controller.eliminarDirectorio(seleccionado.getIdDirectorio());
                        cargarDirectorios();
                        mostrarEstado(lblEstado,
                                "✔ Directorio eliminado.", "#15803d");
                    } catch (Exception ex) {
                        mostrarEstado(lblEstado,
                                "✘ Error al eliminar: " + ex.getMessage(), "#b91c1c");
                    }
                }
            });
        });

        getChildren().addAll(
                titulo,
                subtitulo,
                new Label("Carpetas indexadas:"),
                listaRutas,
                panelInput,
                btnEliminar,
                lblEstado
        );
    }

    // ─── Helpers ──────────────────────────────────────────────

    /**
     * Carga (o recarga) la lista de directorios desde la BD.
     */
    private void cargarDirectorios() {
        try {
            List<Directorio> lista = controller.obtenerDirectorios();
            directorios.setAll(lista);

            listaRutas.getItems().clear();
            for (Directorio d : lista) {
                listaRutas.getItems().add(
                        d.getRutaDirectorio() + "  [" + d.getEstado() + "]"
                );
            }
        } catch (Exception e) {
            listaRutas.getItems().setAll(
                    "⚠ Error al cargar: " + e.getMessage()
            );
        }
    }

    private void mostrarEstado(Label lbl, String mensaje, String color) {
        lbl.setText(mensaje);
        lbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + color + ";");
    }
}