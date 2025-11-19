package co.edu.uptc.controller;

import co.edu.uptc.entity.Producto;
import co.edu.uptc.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller web para interfaz HTML con Thymeleaf
 * Proporciona páginas web para gestionar productos
 */
@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    @Autowired
    private ProductoService productoService;

    /**
     * Página principal - Lista de productos
     */
    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("valorInventario", productoService.calcularValorInventario());
        return "productos/lista";
    }

    /**
     * Mostrar formulario para crear producto
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("accion", "Crear");
        return "productos/formulario";
    }

    /**
     * Procesar creación de producto
     */
    @PostMapping("/crear")
    public String crearProducto(@Valid @ModelAttribute Producto producto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("accion", "Crear");
            return "productos/formulario";
        }

        try {
            productoService.crearProducto(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        return "redirect:/productos";
    }

    /**
     * Mostrar formulario para editar producto
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> producto = productoService.buscarProductoPorId(id);

        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            model.addAttribute("accion", "Editar");
            return "productos/formulario";
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Producto no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/productos";
        }
    }

    /**
     * Procesar actualización de producto
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id,
            @Valid @ModelAttribute Producto producto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("accion", "Editar");
            return "productos/formulario";
        }

        try {
            productoService.actualizarProducto(id, producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        return "redirect:/productos";
    }

    /**
     * Eliminar producto
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        return "redirect:/productos";
    }

    /**
     * Ver detalles de un producto
     */
    @GetMapping("/ver/{id}")
    public String verProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> producto = productoService.buscarProductoPorId(id);

        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "productos/detalle";
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Producto no encontrado");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/productos";
        }
    }

    /**
     * Buscar productos
     */
    @GetMapping("/buscar")
    public String buscarProductos(@RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            Model model) {
        List<Producto> productos;
        String criterioBusqueda = "";

        if (nombre != null && !nombre.trim().isEmpty()) {
            productos = productoService.buscarPorNombre(nombre);
            criterioBusqueda = "nombre: " + nombre;
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            productos = productoService.buscarPorCategoria(categoria);
            criterioBusqueda = "categoría: " + categoria;
        } else {
            productos = productoService.obtenerTodosLosProductos();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("criterioBusqueda", criterioBusqueda);
        model.addAttribute("totalProductos", productos.size());
        return "productos/lista";
    }
}