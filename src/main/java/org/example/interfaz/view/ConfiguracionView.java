package org.example.interfaz.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import org.example.interfaz.controllers.MainController;
import org.example.model.DirectorioVista;

import java.io.File;
import java.util.List;

public class ConfiguracionView extends VBox {

    private final MainController controller;
    private final ListView<String> listaRutas;
    private final javafx.collections.ObservableList<DirectorioVista> directorios =
            javafx.collections.FXCollections.observableArrayList();

    public ConfiguracionView(MainController controller) {

        this.controller = controller;
        this.listaRutas = new ListView<>();

        setSpacing(15);
        setPadding(new Insets(30));

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

        listaRutas.setPrefHeight(220);
        listaRutas.setPlaceholder(new Label(
                "No hay directorios configurados.\nAgrega una carpeta y sincroniza el índice."
        ));
        VBox.setVgrow(listaRutas, Priority.ALWAYS);

        cargarDirectorios();

        TextField txtNuevaRuta = new TextField();
        txtNuevaRuta.setPromptText("Ej: C:/Documentos/Proyectos");
        txtNuevaRuta.setStyle(
                "-fx-padding:10;" +
                "-fx-border-color:#cbd5e1;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );
        HBox.setHgrow(txtNuevaRuta, Priority.ALWAYS);

        Button btnExplorar = new Button("📁 Explorar");
        btnExplorar.setStyle(
                "-fx-background-color:#e2e8f0;" +
                "-fx-text-fill:#334155;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:10 14;"
        );

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

        Button btnSincronizar = new Button("🔄 Sincronizar índice");
        btnSincronizar.setStyle(
                "-fx-background-color:#15803d;" +
                "-fx-text-fill:white;" +
                "-fx-background-radius:8;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:10 18;"
        );

        HBox panelInput = new HBox(10, txtNuevaRuta, btnExplorar, btnAgregar);
        panelInput.setAlignment(Pos.CENTER_LEFT);

        Label lblEstado = new Label("");
        lblEstado.setStyle("-fx-font-size:12px;");

        btnExplorar.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Seleccionar carpeta a indexar");
            File seleccionado = chooser.showDialog(getScene().getWindow());
            if (seleccionado != null) {
                txtNuevaRuta.setText(seleccionado.getAbsolutePath());
            }
        });

        btnAgregar.setOnAction(e -> {
            String ruta = txtNuevaRuta.getText().trim();

            if (ruta.isEmpty()) {
                mostrarEstado(lblEstado, "⚠ Escribe o selecciona una ruta antes de agregar.", "#b45309");
                return;
            }

            boolean duplicada = directorios.stream()
                    .anyMatch(d -> d.getDirectorio().getRutaDirectorio().equalsIgnoreCase(ruta));

            if (duplicada) {
                mostrarEstado(lblEstado, "⚠ Esa ruta ya está en la lista.", "#b45309");
                return;
            }

            try {
                controller.agregarDirectorio(ruta);
                txtNuevaRuta.clear();
                cargarDirectorios();
                mostrarEstado(lblEstado, "✔ Directorio agregado correctamente.", "#15803d");
            } catch (Exception ex) {
                mostrarEstado(lblEstado, "✘ Error al agregar: " + ex.getMessage(), "#b91c1c");
            }
        });

        btnEliminar.setOnAction(e -> {
            int idx = listaRutas.getSelectionModel().getSelectedIndex();

            if (idx < 0) {
                mostrarEstado(lblEstado, "⚠ Selecciona una ruta de la lista primero.", "#b45309");
                return;
            }

            DirectorioVista seleccionado = directorios.get(idx);

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar \"" + seleccionado.getDirectorio().getRutaDirectorio() + "\"?",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setHeaderText("Confirmar eliminación");

            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.YES) {
                    try {
                        controller.eliminarDirectorio(seleccionado.getDirectorio().getIdDirectorio());
                        cargarDirectorios();
                        mostrarEstado(lblEstado, "✔ Directorio eliminado.", "#15803d");
                    } catch (Exception ex) {
                        mostrarEstado(lblEstado, "✘ Error al eliminar: " + ex.getMessage(), "#b91c1c");
                    }
                }
            });
        });

        btnSincronizar.setOnAction(e -> {
            if (directorios.isEmpty()) {
                mostrarEstado(lblEstado, "⚠ Agrega al menos un directorio antes de sincronizar.", "#b45309");
                return;
            }

            btnSincronizar.setDisable(true);
            mostrarEstado(lblEstado, "⏳ Sincronizando índice...", "#64748b");

            Thread sincronizacion = new Thread(() -> {
                try {
                    controller.sincronizarIndice();
                    javafx.application.Platform.runLater(() -> {
                        cargarDirectorios();
                        mostrarEstado(lblEstado, "✔ Índice sincronizado correctamente.", "#15803d");
                        btnSincronizar.setDisable(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        mostrarEstado(lblEstado, "✘ Error al sincronizar: " + ex.getMessage(), "#b91c1c");
                        btnSincronizar.setDisable(false);
                    });
                }
            });
            sincronizacion.setDaemon(true);
            sincronizacion.start();
        });

        getChildren().addAll(
                titulo,
                subtitulo,
                new Label("Directorios indexados:"),
                listaRutas,
                panelInput,
                btnSincronizar,
                btnEliminar,
                lblEstado
        );
    }

    private void cargarDirectorios() {
        try {
            List<DirectorioVista> lista = controller.obtenerDirectoriosIndexados();
            directorios.setAll(lista);

            listaRutas.getItems().clear();
            for (DirectorioVista d : lista) {
                listaRutas.getItems().add(d.getTextoLista());
            }
        } catch (Exception e) {
            listaRutas.getItems().setAll("⚠ Error al cargar: " + e.getMessage());
        }
    }

    private void mostrarEstado(Label lbl, String mensaje, String color) {
        lbl.setText(mensaje);
        lbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + color + ";");
    }
}
