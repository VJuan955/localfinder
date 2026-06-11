package org.example.interfaz;

import org.example.interfaz.views.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    private BorderPane root;

    @Override
    public void start(Stage stage) {

        root = new BorderPane();

        // ===== MENÚ LATERAL =====
        VBox menu = new VBox(10);
        menu.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-padding: 20;"
        );
        menu.setPrefWidth(180);

        Button btnBusqueda = crearBoton("🔍 Búsqueda");
        Button btnConfig = crearBoton("⚙ Configuración");
        Button btnEstado = crearBoton("📊 Estado");
        Button btnHistorial = crearBoton("🕒 Historial");

        menu.getChildren().addAll(
                btnBusqueda,
                btnConfig,
                btnEstado,
                btnHistorial
        );

        // ===== VISTAS =====
        BusquedaView busquedaView = new BusquedaView();
        ConfiguracionView configView = new ConfiguracionView();
        EstadoView estadoView = new EstadoView();
        HistorialView historialView = new HistorialView();

        // ===== ACCIONES =====
        btnBusqueda.setOnAction(e -> root.setCenter(busquedaView));
        btnConfig.setOnAction(e -> root.setCenter(configView));
        btnEstado.setOnAction(e -> root.setCenter(estadoView));
        btnHistorial.setOnAction(e -> root.setCenter(historialView));

        // Vista inicial
        root.setLeft(menu);
        root.setCenter(busquedaView);

        Scene scene = new Scene(root, 1000, 650);

        stage.setTitle("LocalFinder");
        stage.setScene(scene);
        stage.show();
    }

    // ===== BOTÓN ESTILO PRO =====
    private Button crearBoton(String texto) {

        Button btn = new Button(texto);

        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-pref-width: 160;"
        );

        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: #1e293b;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-pref-width: 160;"
                )
        );

        btn.setOnMouseExited(e ->
                btn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-pref-width: 160;"
                )
        );

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}