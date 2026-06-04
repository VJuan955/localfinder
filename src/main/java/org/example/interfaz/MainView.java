package org.example.interfaz;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.example.interfaz.views.*;

public class MainView {

    private BorderPane root;
    private boolean oscuro = true;

    public MainView() {

        root = new BorderPane();

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(220);

        aplicarTemaOscuro(menu);

        Button btnBusqueda =
                new Button("Motor de Búsqueda");

        Button btnConfig =
                new Button("Configuración");

        Button btnHistorial =
                new Button("Historial");

        Button btnEstado =
                new Button("Estado");

        Button btnTema =
                new Button("🌙 Tema");

        menu.getChildren().addAll(
                btnBusqueda,
                btnConfig,
                btnHistorial,
                btnEstado,
                btnTema
        );

        root.setLeft(menu);

        root.setCenter(
                new BusquedaView()
        );

        btnBusqueda.setOnAction(e ->
                root.setCenter(new BusquedaView())
        );

        btnConfig.setOnAction(e ->
                root.setCenter(new ConfiguracionView())
        );

        btnHistorial.setOnAction(e ->
                root.setCenter(new HistorialView())
        );

        btnEstado.setOnAction(e ->
                root.setCenter(new EstadoView())
        );

        btnTema.setOnAction(e -> {

            oscuro = !oscuro;

            if (oscuro) {

                aplicarTemaOscuro(menu);

                root.setStyle(
                        "-fx-background-color:#0f172a;"
                );

            } else {

                menu.setStyle(
                        "-fx-background-color:#f4f4f4;"
                );

                root.setStyle(
                        "-fx-background-color:white;"
                );
            }
        });
    }

    private void aplicarTemaOscuro(VBox menu) {

        menu.setStyle(
                "-fx-background-color:#0f172a;"
        );
    }

    public Parent getRoot() {
        return root;
    }
}