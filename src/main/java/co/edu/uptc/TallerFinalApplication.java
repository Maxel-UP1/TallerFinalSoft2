package co.edu.uptc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para el sistema CRUD de productos
 * Parte 1: Construcción de la Aplicación Base (Sin Seguridad)
 */
@SpringBootApplication
public class TallerFinalApplication {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SISTEMA CRUD DE PRODUCTOS - SPRING BOOT");
        System.out.println("   Taller Final - Parte 1 (Sin Seguridad)");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Interfaz Web: http://localhost:8080/productos");
        System.out.println("API REST:     http://localhost:8080/api/productos");
        System.out.println();

        SpringApplication.run(TallerFinalApplication.class, args);
    }
}