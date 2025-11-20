package co.edu.uptc.config;

import co.edu.uptc.interceptor.ShiroSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web que registra el interceptor de seguridad Apache Shiro
 * Compatible con Spring Boot 3
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ShiroSecurityInterceptor shiroSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("🔧 CONFIGURANDO interceptor de seguridad Apache Shiro");

        registry.addInterceptor(shiroSecurityInterceptor)
                .addPathPatterns("/**") // Aplicar a todas las rutas
                .excludePathPatterns( // EXCEPTO estas rutas públicas:
                        "/login", // Página de login
                        "/logout", // Logout
                        "/register", // Registro de usuarios - Parte 3
                        "/api/user-stats", // Estadísticas públicas - Parte 3
                        "/css/**", // Recursos CSS
                        "/js/**", // Recursos JS
                        "/images/**", // Imágenes
                        "/webjars/**", // Bootstrap/jQuery vía WebJars
                        "/h2-console/**", // Consola H2 (desarrollo)
                        "/favicon.ico" // Icono del sitio
                );
    }
}