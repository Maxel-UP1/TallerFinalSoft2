package co.edu.uptc.interceptor;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor que integra Apache Shiro con Spring Boot 3
 * Maneja la autenticación y autorización usando Apache Shiro SecurityUtils
 */
@Component
public class ShiroSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Rutas que NO requieren autenticación
        if (isPublicPath(path)) {
            return true;
        }

        // Verificar autenticación usando Apache Shiro
        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            // Usuario no autenticado - redirigir al login
            System.out.println("🔒 SHIRO INTERCEPTOR: Usuario no autenticado, redirigiendo a /login");
            System.out.println("   📍 Ruta solicitada: " + path);
            response.sendRedirect(contextPath + "/login");
            return false;
        }

        // Usuario autenticado - permitir acceso
        System.out.println("✅ SHIRO INTERCEPTOR: Usuario autenticado - " + currentUser.getPrincipal());
        System.out.println("   📍 Accediendo a: " + path);
        return true;
    }

    /**
     * Determina si una ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        return path.equals("/login") ||
                path.equals("/logout") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/h2-console/") ||
                path.equals("/favicon.ico");
    }
}