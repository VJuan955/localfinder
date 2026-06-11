package org.example.interfaz;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.example.interfaz.views.*;

public class MainView {

    private BorderPane root;
    private VBox menu;
    private boolean oscuro = true;

    public MainView() {

        root = new BorderPane();

        // =========================
        // MENÚ LATERAL
        // =========================
        menu = new VBox(12);
        menu.setPadding(new Insets(20));
        menu.setPrefWidth(220);

        aplicarTemaOscuro();

        Button btnBusqueda = crearBoton("🔍 Búsqueda");
        Button btnConfig = crearBoton("⚙ Configuración");
        Button btnHistorial = crearBoton("🕒 Historial");
        Button btnEstado = crearBoton("📊 Estado");
        Button btnTema = crearBoton("🌙 Cambiar tema");

        menu.getChildren().addAll(
                btnBusqueda,
                btnConfig,
                btnHistorial,
                btnEstado,
                btnTema
        );

        // =========================
        // VISTAS
        // =========================
        BusquedaView busquedaView = new BusquedaView();
        ConfiguracionView configView = new ConfiguracionView();
        HistorialView historialView = new HistorialView();
        EstadoView estadoView = new EstadoView();

        root.setLeft(menu);
        root.setCenter(busquedaView);

        // =========================
        // NAVEGACIÓN
        // =========================
        btnBusqueda.setOnAction(e -> root.setCenter(busquedaView));
        btnConfig.setOnAction(e -> root.setCenter(configView));
        btnHistorial.setOnAction(e -> root.setCenter(historialView));
        btnEstado.setOnAction(e -> root.setCenter(estadoView));

        // =========================
        // TEMA
        // =========================
        btnTema.setOnAction(e -> {

            oscuro = !oscuro;

            if (oscuro) {
                aplicarTemaOscuro();
            } else {
                aplicarTemaClaro();
            }
        });
    }

    // =====================================================
    // 🎨 BOTÓN PRO
    // =====================================================
    private Button crearBoton(String texto) {

        Button btn = new Button(texto);

        btn.setPrefWidth(180);

        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 10;"
        );

        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: #1e293b;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;"
                )
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;"
                )
        );

        return btn;
    }

    // =====================================================
    // 🌙 TEMA OSCURO
    // =====================================================
    private void aplicarTemaOscuro() {

        menu.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-border-color: #1e293b;"
        );

        root.setStyle("-fx-background-color: #0b1220;");
    }

    // =====================================================
    // ☀ TEMA CLARO
    // =====================================================
    private void aplicarTemaClaro() {

        menu.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #e2e8f0;"
        );

        root.setStyle("-fx-background-color: white;");
    }

    // =====================================================
    // ROOT
    // =====================================================
    public Parent getRoot() {
        return root;
    }
}