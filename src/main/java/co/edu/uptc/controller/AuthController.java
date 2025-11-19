package co.edu.uptc.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller para manejar autenticación con Apache Shiro REAL
 * Parte 2: Integración de Apache Shiro
 */
@Controller
public class AuthController {

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
     * Procesa el login del usuario usando Apache Shiro
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            try {
                currentUser.login(token);
                redirectAttributes.addFlashAttribute("mensaje", "Bienvenido " + username);
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
                return "redirect:/productos";
            } catch (AuthenticationException e) {
                redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
                return "redirect:/login";
            }
        }

        return "redirect:/productos";
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
}