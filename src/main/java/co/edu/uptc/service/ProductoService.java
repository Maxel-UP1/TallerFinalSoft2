package co.edu.uptc.service;

import co.edu.uptc.model.Producto;
import co.edu.uptc.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para productos
 * Aquí se aplican validaciones y reglas de negocio
 */
public class ProductoService {
    private ProductoRepository repository;

    public ProductoService() {
        this.repository = new ProductoRepository();
    }

    /**
     * CREAR un nuevo producto con validaciones
     */
    public Producto crearProducto(Producto producto) {
        // Validar datos del producto
        validarProducto(producto);

        // Limpiar y formatear datos
        producto.setNombre(producto.getNombre().trim());
        if (producto.getDescripcion() != null) {
            producto.setDescripcion(producto.getDescripcion().trim());
        }
        if (producto.getCategoria() != null) {
            producto.setCategoria(producto.getCategoria().trim());
        }

        return repository.crear(producto);
    }

    /**
     * OBTENER todos los productos
     */
    public List<Producto> obtenerTodosLosProductos() {
        return repository.obtenerTodos();
    }

    /**
     * BUSCAR producto por ID
     */
    public Optional<Producto> buscarProductoPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }
        return repository.buscarPorId(id);
    }

    /**
     * ACTUALIZAR un producto existente
     */
    public Producto actualizarProducto(Long id, Producto producto) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }

        // Verificar que el producto existe
        Optional<Producto> productoExistente = repository.buscarPorId(id);
        if (productoExistente.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }

        // Validar nuevos datos
        validarProducto(producto);

        // Mantener el ID y fecha de creación original
        producto.setId(id);
        producto.setFechaCreacion(productoExistente.get().getFechaCreacion());

        // Limpiar datos
        producto.setNombre(producto.getNombre().trim());
        if (producto.getDescripcion() != null) {
            producto.setDescripcion(producto.getDescripcion().trim());
        }
        if (producto.getCategoria() != null) {
            producto.setCategoria(producto.getCategoria().trim());
        }

        return repository.actualizar(producto);
    }

    /**
     * ELIMINAR un producto
     */
    public boolean eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo");
        }

        // Verificar que el producto existe
        Optional<Producto> producto = repository.buscarPorId(id);
        if (producto.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }

        return repository.eliminar(id);
    }

    /**
     * BUSCAR productos por nombre
     */
    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        return repository.buscarPorNombre(nombre.trim());
    }

    /**
     * Método privado para validar un producto
     */
    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (producto.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres");
        }

        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a 0");
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser mayor o igual a 0");
        }
    }
}