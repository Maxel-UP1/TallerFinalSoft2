package co.edu.uptc.service;

import co.edu.uptc.entity.Producto;
import co.edu.uptc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de productos usando Spring Boot
 * Contiene la lógica de negocio y validaciones
 */
@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * CREAR - Crear un nuevo producto
     */
    public Producto crearProducto(Producto producto) {
        // Validar que no exista un producto con el mismo nombre
        if (productoRepository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }

        return productoRepository.save(producto);
    }

    /**
     * LEER - Obtener todos los productos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    /**
     * LEER - Buscar producto por ID
     */
    @Transactional(readOnly = true)
    public Optional<Producto> buscarProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * ACTUALIZAR - Actualizar un producto existente
     */
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    // Verificar nombre único (excluyendo el producto actual)
                    if (!producto.getNombre().equalsIgnoreCase(productoActualizado.getNombre()) &&
                            productoRepository.existsByNombreIgnoreCase(productoActualizado.getNombre())) {
                        throw new IllegalArgumentException(
                                "Ya existe otro producto con el nombre: " + productoActualizado.getNombre());
                    }

                    // Actualizar campos
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setStock(productoActualizado.getStock());
                    producto.setCategoria(productoActualizado.getCategoria());

                    return productoRepository.save(producto);
                })
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    /**
     * ELIMINAR - Eliminar un producto por ID
     */
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * BUSCAR - Buscar productos por nombre
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /**
     * BUSCAR - Buscar productos por categoría
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    /**
     * BUSCAR - Buscar productos por rango de precio
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax);
    }

    /**
     * ESTADÍSTICAS - Obtener productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosStockBajo(Integer stockMinimo) {
        return productoRepository.findProductosConStockBajo(stockMinimo);
    }

    /**
     * ESTADÍSTICAS - Calcular valor total del inventario
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularValorInventario() {
        BigDecimal valor = productoRepository.calcularValorTotalInventario();
        return valor != null ? valor : BigDecimal.ZERO;
    }

    /**
     * ESTADÍSTICAS - Contar productos por categoría
     */
    @Transactional(readOnly = true)
    public Long contarPorCategoria(String categoria) {
        return productoRepository.countByCategoria(categoria);
    }

    /**
     * UTILIDAD - Verificar si un producto existe
     */
    @Transactional(readOnly = true)
    public boolean existeProducto(Long id) {
        return productoRepository.existsById(id);
    }

    /**
     * UTILIDAD - Obtener total de productos
     */
    @Transactional(readOnly = true)
    public long contarTotalProductos() {
        return productoRepository.count();
    }
}