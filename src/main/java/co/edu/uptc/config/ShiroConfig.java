package co.edu.uptc.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Apache Shiro con Base de Datos - Parte 3
 * Utiliza DatabaseRealm para autenticación segura con contraseñas hasheadas
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private DatabaseRealm databaseRealm;

    /**
     * Configura el SessionManager para entorno web
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * Configura el SecurityManager con DatabaseRealm y SessionManager web
     */
    @Bean
    public SecurityManager securityManager() {
        System.out.println("🔧 CONFIGURANDO Apache Shiro SecurityManager con DatabaseRealm");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(databaseRealm);
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }
}