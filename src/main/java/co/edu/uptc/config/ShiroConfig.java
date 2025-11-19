package co.edu.uptc.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Apache Shiro como backend de autenticación
 * Se integra con Spring Boot 3 usando interceptors en lugar de filtros
 */
@Configuration
public class ShiroConfig {

    /**
     * Configura el Realm basado en archivo INI
     */
    @Bean
    public IniRealm realm() {
        System.out.println(" CONFIGURANDO Apache Shiro IniRealm");
        IniRealm realm = new IniRealm("classpath:shiro.ini");
        return realm;
    }

    /**
     * Configura el SecurityManager de Shiro
     */
    @Bean
    public SecurityManager securityManager() {
        System.out.println(" CONFIGURANDO Apache Shiro SecurityManager");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        return securityManager;
    }
}