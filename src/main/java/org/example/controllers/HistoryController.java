package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.example.dao.BusquedaDAO;
import org.example.dao.impl.BusquedaDAOImpl;
import org.example.dao.ResultadoDAO;
import org.example.dao.impl.ResultadoDAOImpl;
import org.example.model.Busqueda;
import org.example.model.Resultado;

public class HistoryController implements Initializable {
    @FXML private Button btnMotorBusqueda;
    @FXML private Button btnConfigIndice;
    @FXML private Button btnHistorial;
    @FXML private Button btnEstado;
    @FXML private Circle crawlerDot;
    @FXML private Label crawlerStatusLabel;

    @FXML private TextField filterField;
    @FXML private VBox tableBody;
    @FXML private Label footerCount;
    @FXML private Label footerUpdate;

    private final BusquedaDAO busquedaDAO = new BusquedaDAOImpl();
    private final ResultadoDAO resultadoDAO = new ResultadoDAOImpl();

    public static class HistoryEntry {
        private final int idBusqueda;
        private final String term;
        private final LocalDateTime date;
        private final int resultCount; // -1 = sin resultados

        public HistoryEntry(int idBusqueda, String term, LocalDateTime date, int resultCount) {
            this.idBusqueda = idBusqueda;
            this.term = term;
            this.date = date;
            this.resultCount = resultCount;
        }

        public int getIdBusqueda() { return idBusqueda; }
        public String getTerm() { return term; }
        public LocalDateTime getDate() { return date; }
        public int getResultCount() { return resultCount; }
        public boolean hasResults() { return resultCount > 0; }
    }

    private final List<HistoryEntry> allEntries = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", new Locale("es", "PE"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCrawlerStatus(true);

        loadDataFromDatabase();

        filterField.textProperty().addListener((obs, oldVal, newVal) ->
                applyFilter(newVal.trim().toLowerCase()));
    }

    private void loadDataFromDatabase() {
        tableBody.getChildren().clear();
        tableBody.getChildren().add(new Label("Cargando historial..."));

        CompletableFuture.supplyAsync(() -> {
            List<HistoryEntry> loadedEntries = new ArrayList<>();
            List<Busqueda> historialDb = busquedaDAO.obtenerHistorial();

            for (Busqueda bq : historialDb) {
                List<Resultado> resultados = resultadoDAO.obtenerPorBusqueda(bq.getIdBusqueda());

                LocalDateTime dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(bq.getFechaBusqueda()),
                        ZoneId.systemDefault()
                );

                String termVisual = bq.getFiltrosAplicados() != null ? bq.getFiltrosAplicados() : "Búsqueda #" + bq.getIdBusqueda();

                loadedEntries.add(new HistoryEntry(
                        bq.getIdBusqueda(),
                        termVisual,
                        dateTime,
                        resultados.size()
                ));
            }
            return loadedEntries;
        }).thenAccept(entries -> Platform.runLater(() -> {
            allEntries.clear();
            allEntries.addAll(entries);
            refreshTable(allEntries);
            updateFooter();
        }));
    }

    private void refreshTable(List<HistoryEntry> entries) {
        tableBody.getChildren().clear();
        for (HistoryEntry entry : entries) {
            tableBody.getChildren().add(buildRow(entry));
        }
        updateFooter(entries.size());
    }

    private HBox buildRow(HistoryEntry entry) {
        Label termLabel = new Label(entry.getTerm());
        termLabel.getStyleClass().add("cell-term");

        Label dateLabel = new Label(entry.getDate().format(DATE_FMT));
        dateLabel.getStyleClass().add("cell-date");

        Label resultsLabel;
        if (entry.hasResults()) {
            resultsLabel = new Label(entry.getResultCount() + " encontrados");
            resultsLabel.getStyleClass().add("cell-results-found");
        } else {
            resultsLabel = new Label("Sin resultados");
            resultsLabel.getStyleClass().add("cell-results-empty");
        }

        Button replayBtn = new Button("↺");
        replayBtn.getStyleClass().addAll("action-btn", "action-btn-replay");
        replayBtn.setTooltip(new Tooltip("Repetir búsqueda"));
        replayBtn.setOnAction(e -> onReplaySearch(entry));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("action-btn", "action-btn-delete");
        deleteBtn.setTooltip(new Tooltip("Eliminar entrada"));
        deleteBtn.setOnAction(e -> onDeleteEntry(entry));

        HBox actionsBox = new HBox(6, replayBtn, deleteBtn);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actionsBox, Priority.ALWAYS);

        HBox row = new HBox(0, termLabel, dateLabel, resultsLabel, actionsBox);
        row.getStyleClass().add("history-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void applyFilter(String query) {
        if (query.isEmpty()) {
            refreshTable(allEntries);
            return;
        }
        List<HistoryEntry> result = allEntries.stream()
                .filter(e -> e.getTerm().toLowerCase().contains(query))
                .collect(Collectors.toList());
        refreshTable(result);
    }

    private void onReplaySearch(HistoryEntry entry) {
        System.out.println("Repetir búsqueda: " + entry.getTerm());
        // TODO: navegar a la Vista 1 y ejecutar la búsqueda
    }

    private void onDeleteEntry(HistoryEntry entry) {
        allEntries.remove(entry);
        applyFilter(filterField.getText().trim().toLowerCase());
    }

    @FXML
    private void onClearHistory() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Limpiar Historial");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Eliminar todas las entradas del historial de forma permanente?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                allEntries.clear();
                refreshTable(allEntries);
            }
        });
    }

    private void updateFooter() {
        updateFooter(allEntries.size());

        String syncTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        footerUpdate.setText("Última actualización: Hoy a las " + syncTime);
    }

    private void updateFooter(int count) {
        footerCount.setText("Mostrando " + count + " consulta" + (count == 1 ? "" : "s"));
    }

    @FXML private void onMotorBusqueda() {
        setActiveNav(btnMotorBusqueda);
        System.out.println("Navegar a: Motor de Búsqueda");
    }

    @FXML private void onConfigIndice() {
        setActiveNav(btnConfigIndice);
        System.out.println("Navegar a: Configuración Índice");
    }

    @FXML private void onHistorial() {
        setActiveNav(btnHistorial);
    }

    @FXML private void onEstado() {
        setActiveNav(btnEstado);
        System.out.println("Navegar a: Estado");
    }

    private void setActiveNav(Button selected) {
        for (Button btn : new Button[]{btnMotorBusqueda, btnConfigIndice, btnHistorial, btnEstado}) {
            btn.getStyleClass().remove("nav-item-active");
        }
        if (!selected.getStyleClass().contains("nav-item-active")) {
            selected.getStyleClass().add("nav-item-active");
        }
    }

    public void setCrawlerStatus(boolean activo) {
        if (activo) {
            crawlerDot.getStyleClass().setAll("crawler-dot-active");
            crawlerStatusLabel.setText("Activo");
            crawlerStatusLabel.getStyleClass().setAll("crawler-active-text");
        } else {
            crawlerDot.getStyleClass().setAll("crawler-dot-inactive");
            crawlerStatusLabel.setText("Inactivo");
            crawlerStatusLabel.getStyleClass().setAll("label");
        }
    }
}
