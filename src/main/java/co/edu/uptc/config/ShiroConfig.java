package co.edu.uptc.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Autowired
    private DatabaseRealm databaseRealm;

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // evita que JSESSIONID se pase por URL
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        // timeout en milisegundos (ej. 30 minutos)
        sessionManager.setGlobalSessionTimeout(30 * 60 * 1000);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(databaseRealm);
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setLoginUrl("/login");
        bean.setUnauthorizedUrl("/unauthorized");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // recursos públicos
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/api/checkRole", "anon"); // endpoint de comprobación público

        // rutas protegidas por rol
        filterChainDefinitionMap.put("/admin/**", "roles[admin]");
        filterChainDefinitionMap.put("/manager/**", "roles[manager]");

        // rutas protegidas por permisos
        filterChainDefinitionMap.put("/productos/nuevo", "perms[producto:create]");
        filterChainDefinitionMap.put("/productos/editar/**", "perms[producto:update]");
        filterChainDefinitionMap.put("/productos/eliminar/**", "perms[producto:delete]");
        filterChainDefinitionMap.put("/api/inventario/**", "perms[inventario:view]");
        filterChainDefinitionMap.put("/usuarios/**", "perms[usuarios:manage]");

        // por defecto autenticado
        filterChainDefinitionMap.put("/**", "authc");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    // Habilita uso de @RequiresRoles / @RequiresPermissions en beans Spring
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
