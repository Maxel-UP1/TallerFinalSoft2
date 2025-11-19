package co.edu.uptc;

import co.edu.uptc.controller.ProductoController;

/**
 * Clase principal que inicia la aplicación CRUD
 * PARTE 1: Sistema sin seguridad
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(">> Iniciando Sistema CRUD de Productos");
        System.out.println(">> Taller Final - Parte 1 (Sin Seguridad)");

        // Crear y iniciar el controlador
        ProductoController controller = new ProductoController();
        controller.iniciarMenu();
    }
}