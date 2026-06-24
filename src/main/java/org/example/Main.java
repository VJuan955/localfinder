package org.example;

import org.example.dao.DatabaseManager;
import org.example.interfaz.MainApp;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.inicializarBaseDeDatos();
        MainApp.main(args);
    }
}
