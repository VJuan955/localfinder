package org.example.interfaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.dao.BusquedaDAO;
import org.example.dao.DirectorioDAO;
import org.example.dao.DatabaseManager;
import org.example.dao.impl.BusquedaDAOImpl;
import org.example.dao.impl.DirectorioDAOImpl;
import org.example.interfaz.controllers.MainController;
import org.example.service.BusquedaService;
import org.example.service.BusquedaServiceImpl;
import org.example.service.DirectorioService;
import org.example.service.DirectorioServiceImpl;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        DatabaseManager.inicializarBaseDeDatos();

        DirectorioDAO directorioDAO = new DirectorioDAOImpl();
        BusquedaDAO busquedaDAO = new BusquedaDAOImpl();

        DirectorioService directorioService = new DirectorioServiceImpl(directorioDAO);
        BusquedaService busquedaService = new BusquedaServiceImpl(busquedaDAO);

        MainController controller = new MainController(directorioService, busquedaService);
        MainView view = new MainView(controller);

        Scene scene = new Scene(
                view.getRoot(),
                900,
                600
        );

        stage.setTitle("LocalFinder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}