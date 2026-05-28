package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        org.example.dao.DatabaseManager.inicializarBaseDeDatos();

        try {
            new WebServer().start();
        } catch (Exception e) {
            System.err.println("Error al iniciar la interfaz web: " + e.getMessage());
        }
    }
}