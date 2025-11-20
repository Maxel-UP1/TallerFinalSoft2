package co.edu.uptc.config;

import co.edu.uptc.entity.Usuario;
import co.edu.uptc.service.PasswordHashService;
import co.edu.uptc.service.UsuarioService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * DatabaseRealm personalizado para Apache Shiro - Parte 3
 * Utiliza la base de datos para autenticación y autorización
 */
@Component
public class DatabaseRealm extends AuthorizingRealm {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordHashService passwordHashService;

    public DatabaseRealm() {
        setName("DatabaseRealm");
        setCredentialsMatcher(new CustomCredentialsMatcher());
        System.out.println("🏗️ CONFIGURANDO DatabaseRealm con autenticación por BD");
    }

    /**
     * Autorización - Define qué puede hacer el usuario autenticado
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) getAvailablePrincipal(principals);

        Optional<Usuario> usuarioOpt = usuarioService.buscarUsuario(username);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ AUTORIZACIÓN: Usuario no encontrado - " + username);
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();

        // Agregar rol del usuario
        authInfo.addRole(usuario.getRol());

        // Agregar permisos basados en el rol
        switch (usuario.getRol().toLowerCase()) {
            case "admin":
                authInfo.addStringPermission("*"); // Todos los permisos
                break;
            case "manager":
                authInfo.addStringPermission("productos:*");
                authInfo.addStringPermission("usuarios:read");
                authInfo.addStringPermission("reportes:read");
                break;
            case "user":
                authInfo.addStringPermission("productos:read");
                authInfo.addStringPermission("productos:create");
                break;
            default:
                authInfo.addStringPermission("productos:read");
        }

        System.out.println("✅ AUTORIZACIÓN EXITOSA:");
        System.out.println("   👤 Usuario: " + username);
        System.out.println("   🎭 Rol: " + usuario.getRol());
        System.out.println("   🔑 Permisos: " + authInfo.getStringPermissions());

        return authInfo;
    }

    /**
     * Autenticación - Verifica las credenciales del usuario
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = String.valueOf(upToken.getPassword());

        System.out.println("🔐 AUTENTICACIÓN DatabaseRealm:");
        System.out.println("   👤 Usuario: " + username);
        System.out.println("   🔍 Buscando en base de datos...");

        Optional<Usuario> usuarioOpt = usuarioService.buscarUsuario(username);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ Usuario no encontrado en BD: " + username);
            throw new UnknownAccountException("Usuario no encontrado: " + username);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar si el usuario está activo
        if (!usuario.isActivo()) {
            System.out.println("❌ Usuario inactivo: " + username);
            throw new LockedAccountException("Usuario inactivo: " + username);
        }

        // Verificar si el usuario está bloqueado
        if (usuario.isBlocked()) {
            System.out.println("🚫 Usuario bloqueado por intentos fallidos: " + username);
            throw new LockedAccountException("Usuario bloqueado por múltiples intentos fallidos: " + username);
        }

        System.out.println("✅ Usuario encontrado en BD:");
        System.out.println("   📧 Email: " + usuario.getEmail());
        System.out.println("   👤 Nombre: " + usuario.getNombreCompleto());
        System.out.println("   🎭 Rol: " + usuario.getRol());
        System.out.println("   🔐 Algoritmo: " + usuario.getAlgoritmoHash());

        // Retornar información de autenticación
        // La verificación de contraseña se hace en CustomCredentialsMatcher
        return new SimpleAuthenticationInfo(
                username, // Principal (lo que identifica al usuario)
                usuario.getPassword(), // Credentials (password hasheado)
                getName() // Realm name
        );
    }

    /**
     * Matcher personalizado de credenciales que usa nuestro servicio de hash
     */
    private class CustomCredentialsMatcher implements org.apache.shiro.authc.credential.CredentialsMatcher {

        @Override
        public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
            UsernamePasswordToken upToken = (UsernamePasswordToken) token;
            String submittedPassword = String.valueOf(upToken.getPassword());
            String storedPassword = (String) info.getCredentials();
            String username = upToken.getUsername();

            System.out.println("🔍 VERIFICANDO CREDENCIALES:");
            System.out.println("   👤 Usuario: " + username);

            // Obtener el usuario para conocer el algoritmo de hash
            Optional<Usuario> usuarioOpt = usuarioService.buscarUsuario(username);

            if (usuarioOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado para verificación");
                return false;
            }

            Usuario usuario = usuarioOpt.get();
            PasswordHashService.HashAlgorithm algoritmo = passwordHashService
                    .getAlgorithmByName(usuario.getAlgoritmoHash());

            System.out.println("   🔐 Algoritmo detectado: " + algoritmo);

            // Usar el servicio de hash para verificar
            boolean matches = passwordHashService.verifyPassword(submittedPassword, storedPassword, algoritmo);

            if (matches) {
                // Actualizar último login y resetear intentos fallidos
                usuarioService.buscarUsuario(username).ifPresent(u -> {
                    u.resetearIntentosFallidos();
                    // Aquí normalmente guardarías en el repositorio, pero el servicio ya lo hace
                });

                System.out.println("✅ CREDENCIALES VÁLIDAS - Login exitoso");
            } else {
                System.out.println("❌ CREDENCIALES INVÁLIDAS - Login fallido");
            }

            return matches;
        }
    }
}