package co.edu.uptc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para el sistema CRUD de productos
 * Parte 2: Integración de Apache Shiro REAL con interceptors
 */
@SpringBootApplication
public class TallerFinalApplication implements CommandLineRunner {

    @Autowired
    private SecurityManager securityManager;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SISTEMA CRUD DE PRODUCTOS - SPRING BOOT");
        System.out.println("   Parte 2: Apache Shiro REAL + Interceptors");
        System.out.println("========================================");
        System.out.println();
        System.out.println("🔐 Login:        http://localhost:8080/login");
        System.out.println("🏠 Interfaz Web: http://localhost:8080/productos");
        System.out.println("🔌 API REST:     http://localhost:8080/api/productos");
        System.out.println();
        System.out.println("👤 Usuarios de prueba:");
        System.out.println("   admin/admin123 (Admin)");
        System.out.println("   manager/manager123 (Manager)");
        System.out.println("   user/user123 (User)");
        System.out.println();

        SpringApplication.run(TallerFinalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializar Apache Shiro SecurityManager
        SecurityUtils.setSecurityManager(securityManager);
        System.out.println("✅ Apache Shiro SecurityManager inicializado correctamente");
        System.out.println("🔒 Sistema de seguridad ACTIVO - interceptors configurados");
    }
}