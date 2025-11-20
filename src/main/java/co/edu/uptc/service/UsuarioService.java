package co.edu.uptc.service;

import co.edu.uptc.entity.Usuario;
import co.edu.uptc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de usuarios - Parte 3
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordHashService passwordHashService;

    /**
     * Crear un nuevo usuario con contraseña hasheada
     */
    public Usuario crearUsuario(String username, String email, String plainPassword,
            String nombreCompleto, String rol,
            PasswordHashService.HashAlgorithm algoritmo) {

        // Validar que no exista el usuario
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + username);
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        // Hashear la contraseña
        String hashedPassword = passwordHashService.hashPassword(plainPassword, algoritmo);

        // Crear el usuario
        Usuario usuario = new Usuario(username, email, hashedPassword, nombreCompleto, rol, algoritmo.name());

        Usuario savedUser = usuarioRepository.save(usuario);

        System.out.println("👤 USUARIO CREADO:");
        System.out.println("   📝 Username: " + savedUser.getUsername());
        System.out.println("   📧 Email: " + savedUser.getEmail());
        System.out.println("   👤 Nombre: " + savedUser.getNombreCompleto());
        System.out.println("   🎭 Rol: " + savedUser.getRol());
        System.out.println("   🔐 Algoritmo: " + savedUser.getAlgoritmoHash());
        System.out.println("   📅 Fecha: " + savedUser.getFechaCreacion());

        return savedUser;
    }

    /**
     * Autenticar usuario con contraseña
     */
    public boolean autenticarUsuario(String credencial, String plainPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameOrEmail(credencial);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ AUTENTICACIÓN FALLIDA: Usuario no encontrado - " + credencial);
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar si el usuario está activo
        if (!usuario.isActivo()) {
            System.out.println("❌ AUTENTICACIÓN FALLIDA: Usuario inactivo - " + credencial);
            return false;
        }

        // Verificar si el usuario está bloqueado
        if (usuario.isBlocked()) {
            System.out.println("🚫 AUTENTICACIÓN FALLIDA: Usuario bloqueado por intentos fallidos - " + credencial);
            return false;
        }

        // Obtener el algoritmo de hash
        PasswordHashService.HashAlgorithm algoritmo = passwordHashService
                .getAlgorithmByName(usuario.getAlgoritmoHash());

        // Verificar la contraseña
        boolean passwordMatches = passwordHashService.verifyPassword(
                plainPassword, usuario.getPassword(), algoritmo);

        if (passwordMatches) {
            // Autenticación exitosa
            usuario.resetearIntentosFallidos();
            usuarioRepository.save(usuario);

            System.out.println("✅ AUTENTICACIÓN EXITOSA:");
            System.out.println("   👤 Usuario: " + usuario.getUsername());
            System.out.println("   🎭 Rol: " + usuario.getRol());
            System.out.println("   🔐 Algoritmo: " + usuario.getAlgoritmoHash());
            System.out.println("   📅 Último login: " + usuario.getFechaUltimoLogin());

            return true;
        } else {
            // Autenticación fallida
            usuario.incrementarIntentosFallidos();
            usuarioRepository.save(usuario);

            System.out.println("❌ AUTENTICACIÓN FALLIDA:");
            System.out.println("   👤 Usuario: " + usuario.getUsername());
            System.out.println("   🔢 Intentos fallidos: " + usuario.getIntentosFallidos());
            System.out.println("   🚫 Bloqueado: " + (usuario.isBlocked() ? "SÍ" : "NO"));

            return false;
        }
    }

    /**
     * Buscar usuario por username o email
     */
    public Optional<Usuario> buscarUsuario(String credencial) {
        return usuarioRepository.findByUsernameOrEmail(credencial);
    }

    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Obtener usuarios por rol
     */
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol);
    }

    /**
     * Cambiar contraseña de usuario
     */
    public boolean cambiarPassword(String username, String oldPassword, String newPassword,
            PasswordHashService.HashAlgorithm nuevoAlgoritmo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar contraseña actual
        PasswordHashService.HashAlgorithm algoritmoActual = passwordHashService
                .getAlgorithmByName(usuario.getAlgoritmoHash());

        if (!passwordHashService.verifyPassword(oldPassword, usuario.getPassword(), algoritmoActual)) {
            return false;
        }

        // Actualizar con nueva contraseña y algoritmo
        String nuevaPasswordHash = passwordHashService.hashPassword(newPassword, nuevoAlgoritmo);
        usuario.setPassword(nuevaPasswordHash);
        usuario.setAlgoritmoHash(nuevoAlgoritmo.name());

        usuarioRepository.save(usuario);

        System.out.println("🔄 CONTRASEÑA ACTUALIZADA:");
        System.out.println("   👤 Usuario: " + usuario.getUsername());
        System.out.println("   🔐 Nuevo algoritmo: " + nuevoAlgoritmo.name());

        return true;
    }

    /**
     * Desbloquear usuario
     */
    public void desbloquearUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);

            System.out.println("🔓 USUARIO DESBLOQUEADO: " + username);
        }
    }

    /**
     * Obtener estadísticas de usuarios
     */
    public String obtenerEstadisticas() {
        long totalUsuarios = usuarioRepository.count();
        long usuariosActivos = usuarioRepository.findByActivoTrue().size();
        long admins = usuarioRepository.countByRolAndActivoTrue("admin");
        long managers = usuarioRepository.countByRolAndActivoTrue("manager");
        long users = usuarioRepository.countByRolAndActivoTrue("user");
        long bloqueados = usuarioRepository.findUsuariosBloqueados().size();

        return String.format("""
                📊 ESTADÍSTICAS DE USUARIOS:
                👥 Total usuarios: %d
                ✅ Usuarios activos: %d
                👑 Administradores: %d
                👔 Managers: %d
                👤 Usuarios regulares: %d
                🚫 Usuarios bloqueados: %d
                """, totalUsuarios, usuariosActivos, admins, managers, users, bloqueados);
    }

    /**
     * Inicializar usuarios por defecto si no existen - Ejecuta automáticamente al
     * iniciar
     */
    @PostConstruct
    @Transactional
    public void inicializarUsuariosDefecto() {
        if (usuarioRepository.count() == 0) {
            System.out.println("🚀 INICIALIZANDO USUARIOS POR DEFECTO...");

            // Crear usuarios con diferentes algoritmos de hash para demostración
            crearUsuario("admin", "admin@uptc.edu.co", "admin123",
                    "Administrador del Sistema", "admin",
                    PasswordHashService.HashAlgorithm.BCRYPT);

            crearUsuario("manager", "manager@uptc.edu.co", "manager123",
                    "Gerente de Operaciones", "manager",
                    PasswordHashService.HashAlgorithm.ARGON2);

            crearUsuario("user", "user@uptc.edu.co", "user123",
                    "Usuario Regular", "user",
                    PasswordHashService.HashAlgorithm.PBKDF2);

            crearUsuario("demo", "demo@uptc.edu.co", "demo123",
                    "Usuario de Demostración", "user",
                    PasswordHashService.HashAlgorithm.SHA512);

            System.out.println("✅ USUARIOS POR DEFECTO CREADOS EXITOSAMENTE");
            System.out.println(passwordHashService.getAlgorithmInfo());
        }
    }

    /**
     * Método sobrecargado para crear usuario desde formulario web
     */
    public Usuario crearUsuario(String username, String email, String plainPassword, String algoritmoString) {
        // Convertir el string del algoritmo al enum
        PasswordHashService.HashAlgorithm algoritmo = passwordHashService.getAlgorithmByName(algoritmoString);

        // Usar el username como nombre completo si no se proporciona
        String nombreCompleto = username.substring(0, 1).toUpperCase() + username.substring(1);

        // Rol por defecto
        String rol = "user";

        return crearUsuario(username, email, plainPassword, nombreCompleto, rol, algoritmo);
    }

    /**
     * Obtener estadísticas de usuarios para el dashboard
     */
    public Map<String, Object> getEstadisticasUsuarios() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Contar total de usuarios
            long totalUsuarios = usuarioRepository.count();
            stats.put("totalUsuarios", totalUsuarios);

            // Contar usuarios activos
            long usuariosActivos = usuarioRepository.countByActivoTrue();
            stats.put("usuariosActivos", usuariosActivos);

            // Contar usuarios por rol
            Map<String, Long> usuariosPorRol = new HashMap<>();
            usuariosPorRol.put("admin", usuarioRepository.countByRol("admin"));
            usuariosPorRol.put("manager", usuarioRepository.countByRol("manager"));
            usuariosPorRol.put("user", usuarioRepository.countByRol("user"));
            stats.put("usuariosPorRol", usuariosPorRol);

            // Contar usuarios por algoritmo de hash
            Map<String, Long> usuariosPorAlgoritmo = new HashMap<>();
            usuariosPorAlgoritmo.put("BCRYPT", usuarioRepository.countByAlgoritmoHash("BCRYPT"));
            usuariosPorAlgoritmo.put("ARGON2", usuarioRepository.countByAlgoritmoHash("ARGON2"));
            usuariosPorAlgoritmo.put("PBKDF2", usuarioRepository.countByAlgoritmoHash("PBKDF2"));
            usuariosPorAlgoritmo.put("SHA512", usuarioRepository.countByAlgoritmoHash("SHA512"));
            stats.put("usuariosPorAlgoritmo", usuariosPorAlgoritmo);

            // Usuarios bloqueados
            long usuariosBloqueados = usuarioRepository.countByIntentosFallidosGreaterThanEqual(5);
            stats.put("usuariosBloqueados", usuariosBloqueados);

            System.out.println("📊 ESTADÍSTICAS DE USUARIOS GENERADAS:");
            System.out.println("   👥 Total: " + totalUsuarios);
            System.out.println("   ✅ Activos: " + usuariosActivos);
            System.out.println("   🚫 Bloqueados: " + usuariosBloqueados);

            return stats;

        } catch (Exception e) {
            System.out.println("❌ ERROR GENERANDO ESTADÍSTICAS: " + e.getMessage());
            e.printStackTrace();
            stats.put("error", "Error al generar estadísticas: " + e.getMessage());
            return stats;
        }
    }
}