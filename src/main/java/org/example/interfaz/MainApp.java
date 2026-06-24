package org.example.interfaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.dao.BusquedaDAOImpl;
import org.example.dao.DirectorioDAOImpl;
import org.example.dao.DatabaseManager;
import org.example.interfaz.controllers.MainController;
import org.example.service.BusquedaServiceImpl;
import org.example.service.DirectorioServiceImpl;
import org.example.service.SistemaBusquedaService;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            DatabaseManager.inicializarBaseDeDatos();

            var directorioDAO = new DirectorioDAOImpl();
            var busquedaDAO = new BusquedaDAOImpl();

            var directorioService = new DirectorioServiceImpl(directorioDAO);
            var busquedaService = new BusquedaServiceImpl(busquedaDAO);
            var sistemaBusquedaService = new SistemaBusquedaService();

            MainController controller = new MainController(
                    directorioService,
                    busquedaService,
                    sistemaBusquedaService
            );
            MainView view = new MainView(controller);

            Scene scene = new Scene(view.getRoot(), 900, 600);

            stage.setTitle("LocalFinder");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar LocalFinder", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
