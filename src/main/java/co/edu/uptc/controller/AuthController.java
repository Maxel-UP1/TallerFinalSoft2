package co.edu.uptc.controller;

import co.edu.uptc.entity.Usuario;
import co.edu.uptc.service.PasswordHashService;
import co.edu.uptc.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para autenticación segura con Apache Shiro - Parte 3
 * Implementa Subject.login() y registro de usuarios con contraseñas hasheadas
 */
@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordHashService passwordHashService;

    /**
     * Muestra la página de login
     */
    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
            Model model, HttpSession session) {

        // Si ya está autenticado con Shiro, redirigir a productos
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return "redirect:/productos";
        }

        if ("unauthorized".equals(error)) {
            model.addAttribute("error", "No tienes permisos para acceder a esta página");
        }
        return "auth/login";
    }

    /**
     * Procesa el login del usuario usando Apache Shiro Subject.login() - Parte 3
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("🔐 INTENTO DE LOGIN:");
        System.out.println("   👤 Usuario: " + username);
        System.out.println("   🌐 IP: " + session.getAttribute("REMOTE_ADDR"));

        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            // Crear token con las credenciales
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(false); // No recordar por seguridad

            try {
                System.out.println("🚀 EJECUTANDO Subject.login() con DatabaseRealm...");

                // ¡AQUÍ ES DONDE OCURRE LA MAGIA DE SUBJECT.LOGIN()!
                currentUser.login(token);

                System.out.println("✅ LOGIN EXITOSO CON Subject.login():");
                System.out.println("   👤 Usuario autenticado: " + currentUser.getPrincipal());
                System.out.println("   🎭 Roles: " + currentUser.hasRole("admin") + " (admin), " +
                        currentUser.hasRole("manager") + " (manager)");
                System.out.println("   🔑 Sesión ID: " + currentUser.getSession().getId());

                redirectAttributes.addFlashAttribute("mensaje",
                        "🎉 Bienvenido " + username + " - Login seguro exitoso!");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                return "redirect:/productos";

            } catch (UnknownAccountException e) {
                System.out.println("❌ LOGIN FALLIDO: Usuario no encontrado - " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "❌ Usuario no encontrado en el sistema");

            } catch (IncorrectCredentialsException e) {
                System.out.println("❌ LOGIN FALLIDO: Contraseña incorrecta - " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "❌ Contraseña incorrecta");

            } catch (LockedAccountException e) {
                System.out.println("🚫 LOGIN FALLIDO: Cuenta bloqueada - " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "🚫 Cuenta bloqueada por seguridad");

            } catch (AuthenticationException e) {
                System.out.println("❌ LOGIN FALLIDO: Error de autenticación - " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "❌ Error de autenticación: " + e.getMessage());
            }
            return "redirect:/login";
        } else {
            // Ya está autenticado con Subject.login()
            System.out.println("ℹ️  Usuario ya autenticado: " + currentUser.getPrincipal());
            redirectAttributes.addFlashAttribute("mensaje",
                    "🔒 Ya estás autenticado como " + currentUser.getPrincipal());
            return "redirect:/productos";
        }
    }

    /**
     * Procesa el logout del usuario usando Apache Shiro
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada exitosamente");
        return "redirect:/login";
    }

    /**
     * Muestra el formulario de registro de usuarios - Parte 3
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Verificar si ya está autenticado
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return "redirect:/productos";
        }

        model.addAttribute("hashAlgorithms", passwordHashService.getAvailableAlgorithms());
        return "auth/register";
    }

    /**
     * Procesa el registro de nuevos usuarios con hasheo seguro - Parte 3
     */
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "hashAlgorithm", defaultValue = "bcrypt") String hashAlgorithm,
            RedirectAttributes redirectAttributes) {

        System.out.println("📝 INTENTO DE REGISTRO:");
        System.out.println("   👤 Usuario: " + username);
        System.out.println("   📧 Email: " + email);
        System.out.println("   🔐 Algoritmo de Hash: " + hashAlgorithm);

        try {
            // Validaciones básicas
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario es obligatorio");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email es obligatorio");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria");
            }
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            // Crear usuario usando el servicio
            Usuario nuevoUsuario = usuarioService.crearUsuario(username, email, password, hashAlgorithm);

            System.out.println("✅ USUARIO REGISTRADO EXITOSAMENTE:");
            System.out.println("   🆔 ID: " + nuevoUsuario.getId());
            System.out.println("   👤 Usuario: " + nuevoUsuario.getUsername());
            System.out.println("   📧 Email: " + nuevoUsuario.getEmail());
            System.out.println("   🔐 Hash Algoritmo: " + nuevoUsuario.getAlgoritmoHash());
            System.out.println("   🎭 Rol: " + nuevoUsuario.getRol());

            redirectAttributes.addFlashAttribute("mensaje",
                    "🎉 Usuario registrado exitosamente con " + hashAlgorithm.toUpperCase()
                            + "! Ahora puedes iniciar sesión.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR DE VALIDACIÓN: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
            return "redirect:/register";

        } catch (Exception e) {
            System.out.println("❌ ERROR INESPERADO EN REGISTRO: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "❌ Error interno del servidor: " + e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * API endpoint para obtener estadísticas de usuarios - Parte 3
     */
    @GetMapping("/api/user-stats")
    @ResponseBody
    public Map<String, Object> getUserStats() {
        try {
            return usuarioService.getEstadisticasUsuarios();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return errorResponse;
        }
    }
}