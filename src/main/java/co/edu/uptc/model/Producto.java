package co.edu.uptc.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa un producto en el sistema
 * Esta es nuestra entidad principal para el CRUD
 */
public class Producto {
    // Atributos del producto
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;
    private LocalDate fechaCreacion;

    // Constructor vacío (necesario para Gson)
    public Producto() {
        this.fechaCreacion = LocalDate.now();
    }

    // Constructor completo
    public Producto(Long id, String nombre, String descripcion, Double precio, Integer stock, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.fechaCreacion = LocalDate.now();
    }

    // Métodos getter y setter para todos los atributos
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos equals y hashCode para comparar productos por ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Método toString para mostrar información del producto
    @Override
    public String toString() {
        return String.format(
                "Producto{id=%d, nombre='%s', precio=%.2f, stock=%d, categoria='%s'}",
                id, nombre, precio, stock, categoria);
    }
}