package co.edu.uptc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para manejar la página de inicio
 */
@Controller
public class HomeController {

    /**
     * Redirige la página principal a la lista de productos
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/productos";
    }
}