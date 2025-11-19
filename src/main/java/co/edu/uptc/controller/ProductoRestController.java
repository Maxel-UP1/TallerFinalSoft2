package co.edu.uptc.controller;

import co.edu.uptc.entity.Producto;
import co.edu.uptc.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller para gestión de productos
 * Expone endpoints para todas las operaciones CRUD
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Para permitir peticiones desde cualquier origen (sin seguridad)
public class ProductoRestController {

    @Autowired
    private ProductoService productoService;

    /**
     * GET /api/productos - Obtener todos los productos
     */
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/{id} - Obtener producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.buscarProductoPorId(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/productos - Crear nuevo producto
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/productos/{id} - Actualizar producto
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
            @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/productos/{id} - Eliminar producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().body("Producto eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * GET /api/productos/buscar?nombre={nombre} - Buscar por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/categoria/{categoria} - Buscar por categoría
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/precio?min={min}&max={max} - Buscar por rango de precio
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> buscarPorRangoPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Producto> productos = productoService.buscarPorRangoPrecio(min, max);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/stock-bajo?minimo={minimo} - Productos con stock bajo
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerStockBajo(@RequestParam(defaultValue = "5") Integer minimo) {
        List<Producto> productos = productoService.obtenerProductosStockBajo(minimo);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener estadísticas generales
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        long totalProductos = productoService.contarTotalProductos();
        BigDecimal valorInventario = productoService.calcularValorInventario();

        var estadisticas = new java.util.HashMap<String, Object>();
        estadisticas.put("totalProductos", totalProductos);
        estadisticas.put("valorTotalInventario", valorInventario);
        estadisticas.put("mensaje", "Estadísticas del inventario");

        return ResponseEntity.ok(estadisticas);
    }
}