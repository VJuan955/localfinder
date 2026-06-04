package org.example.interfaz.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BusquedaView extends VBox {

    public BusquedaView() {

        setSpacing(20);
        setPadding(new Insets(40));
        setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("🔎 LocalFinder");

        titulo.setStyle(
                "-fx-font-size: 38px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        Label subtitulo = new Label(
                "Busca documentos locales de forma rápida y segura"
        );

        subtitulo.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;"
        );

        TextField txtBuscar = new TextField();

        txtBuscar.setPromptText(
                "Buscar archivos, documentos o palabras clave..."
        );

        txtBuscar.setMaxWidth(650);

        txtBuscar.setStyle(
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-padding: 12;" +
                "-fx-font-size: 14px;" +
                "-fx-border-color: #cbd5e1;"
        );

        Button filtroPdf = new Button("PDF");
        Button filtroDocx = new Button("DOCX");
        Button filtroTxt = new Button("TXT");

        String estiloFiltro =
                "-fx-background-color:#e2e8f0;" +
                "-fx-background-radius:20;" +
                "-fx-text-fill:#334155;" +
                "-fx-font-weight:bold;";

        filtroPdf.setStyle(estiloFiltro);
        filtroDocx.setStyle(estiloFiltro);
        filtroTxt.setStyle(estiloFiltro);

        HBox filtros = new HBox(10);
        filtros.setAlignment(Pos.CENTER);

        filtros.getChildren().addAll(
                filtroPdf,
                filtroDocx,
                filtroTxt
        );

        VBox card1 = crearTarjeta(
                "📄 Reporte_Anual.pdf",
                "C:/Documentos/Reportes/Reporte_Anual.pdf",
                "2.4 MB • Modificado 12/05/2026"
        );

        VBox card2 = crearTarjeta(
                "📄 Tesis_Final.docx",
                "C:/Universidad/Tesis/Tesis_Final.docx",
                "1.1 MB • Modificado 01/06/2026"
        );

        VBox card3 = crearTarjeta(
                "📄 Notas_Clase.txt",
                "D:/Cursos/Java/Notas_Clase.txt",
                "350 KB • Modificado 03/06/2026"
        );

        getChildren().addAll(
                titulo,
                subtitulo,
                txtBuscar,
                filtros,
                card1,
                card2,
                card3
        );
    }

    private VBox crearTarjeta(
            String nombre,
            String ruta,
            String detalle
    ) {

        VBox card = new VBox(6);

        card.setPadding(new Insets(18));

        card.setMaxWidth(700);

        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:15;" +
                "-fx-border-radius:15;" +
                "-fx-border-color:#e2e8f0;"
        );

        Label archivo = new Label(nombre);

        archivo.setStyle(
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#0f172a;"
        );

        Label rutaArchivo = new Label(ruta);

        rutaArchivo.setStyle(
                "-fx-text-fill:#475569;"
        );

        Label info = new Label(detalle);

        info.setStyle(
                "-fx-text-fill:#64748b;"
        );

        card.getChildren().addAll(
                archivo,
                rutaArchivo,
                info
        );

        return card;
    }
}