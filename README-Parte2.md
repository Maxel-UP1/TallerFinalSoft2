# Taller Final - Parte 2: Apache Shiro REAL

## ✅ IMPLEMENTACIÓN COMPLETADA CON ÉXITO

### 🎯 Objetivos Cumplidos
- ✅ **Dependencias de Apache Shiro**: Agregadas versiones reales (shiro-core, shiro-web, shiro-spring 1.13.0)
- ✅ **Configuración de SecurityManager**: Implementado con DefaultWebSecurityManager
- ✅ **Configuración de filtros**: ShiroFilterFactoryBean con cadena de filtros completa
- ✅ **Realm elegido**: IniRealm configurado con archivo shiro.ini
- ✅ **Compatibilidad Spring Boot 3**: Resueltos los problemas de javax.servlet vs jakarta.servlet

### 🔧 Dependencias Agregadas

```xml
<!-- Apache Shiro Dependencies - Real Implementation -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.13.0</version>
</dependency>

<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-web</artifactId>
    <version>1.13.0</version>
</dependency>

<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.13.0</version>
</dependency>

<!-- Dependencias de compatibilidad Jakarta/Javax -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
</dependency>

<!-- Javax Servlet API for Apache Shiro compatibility -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
```

### ⚙️ Configuración de SecurityManager

```java
@Configuration
public class ShiroConfig {

    @Bean
    public IniRealm realm() {
        IniRealm realm = new IniRealm("classpath:shiro.ini");
        return realm;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager());
        
        // URLs de login y success
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/productos");
        filterFactoryBean.setUnauthorizedUrl("/login");
        
        // Configuración de filtros por URL
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/images/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/h2-console/**", "anon");
        filterChainDefinitionMap.put("/api/**", "authc");
        filterChainDefinitionMap.put("/**", "authc");
        
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return filterFactoryBean;
    }
}
```

### 🔐 Realm: IniRealm

Configuración en `shiro.ini`:

```ini
# Usuarios y contraseñas
[users]
admin = admin123, admin
manager = manager123, manager
user = user123, user

# Roles y permisos
[roles]
admin = *
manager = producto:*
user = producto:read
```

### 🛠️ Problemas Resueltos

1. **javax.servlet.Filter ClassNotFoundException**
   - **Causa**: Apache Shiro 1.13.0 usa javax.servlet, Spring Boot 3 usa jakarta.servlet
   - **Solución**: Agregada dependencia `javax.servlet-api 4.0.1`

2. **Null PermissionResolver is not allowed**
   - **Causa**: Intentar establecer null explícitamente en PermissionResolver
   - **Solución**: Removida la configuración explícita de PermissionResolver

3. **Spring Boot 3 Compatibility**
   - **Causa**: Incompatibilidad entre versiones de Apache Shiro y Spring Boot 3
   - **Solución**: Configuración híbrida con dependencias javax y jakarta

### 🚀 Funcionalidades Implementadas

- **Autenticación real** con Apache Shiro SecurityManager
- **Autorización basada en roles** (admin, manager, user)
- **Filtros de seguridad** para diferentes URLs
- **Logout automático** mediante filtros Shiro
- **Protección de API REST** con filtro authc
- **Páginas públicas** (login, recursos estáticos)

### 🔧 URLs de Acceso

- **Login**: http://localhost:8080/login
- **Interfaz Web**: http://localhost:8080/productos (requiere autenticación)
- **API REST**: http://localhost:8080/api/productos (requiere autenticación)
- **H2 Console**: http://localhost:8080/h2-console (público para desarrollo)

### 👤 Usuarios de Prueba

| Usuario | Contraseña | Rol     | Permisos         |
| ------- | ---------- | ------- | ---------------- |
| admin   | admin123   | admin   | Todos (*)        |
| manager | manager123 | manager | Productos (CRUD) |
| user    | user123    | user    | Solo lectura     |

### ✅ Estado Final

- ✅ **Apache Shiro REAL implementado** (no simulación)
- ✅ **SecurityManager configurado** con DefaultWebSecurityManager
- ✅ **Filtros de seguridad** funcionando correctamente
- ✅ **IniRealm configurado** con usuarios y roles
- ✅ **Compatibilidad Spring Boot 3** lograda
- ✅ **Aplicación funcionando** sin errores

## 🎉 PARTE 2 COMPLETADA EXITOSAMENTE

La implementación real de Apache Shiro ha sido completada con éxito, cumpliendo todos los requisitos solicitados:

1. ✅ **Dependencias de Shiro agregadas**
2. ✅ **SecurityManager configurado** 
3. ✅ **Filtros implementados**
4. ✅ **IniRealm elegido y configurado**

El sistema ahora utiliza **Apache Shiro REAL** para la autenticación y autorización, no una simulación educativa.