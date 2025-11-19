package co.edu.uptc.repository;

import co.edu.uptc.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para Producto usando Spring Data JPA
 * Proporciona todas las operaciones CRUD automáticamente
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Buscar productos por nombre (contiene - case insensitive)
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Buscar productos por categoría
     */
    List<Producto> findByCategoriaIgnoreCase(String categoria);

    /**
     * Buscar productos en un rango de precios
     */
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    /**
     * Buscar productos con stock mayor a un valor
     */
    List<Producto> findByStockGreaterThan(Integer stock);

    /**
     * Contar productos por categoría
     */
    Long countByCategoria(String categoria);

    /**
     * Query personalizada: productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);

    /**
     * Query personalizada: valor total del inventario
     */
    @Query("SELECT SUM(p.precio * p.stock) FROM Producto p")
    BigDecimal calcularValorTotalInventario();

    /**
     * Buscar productos por categoría ordenados por precio
     */
    List<Producto> findByCategoriaOrderByPrecioAsc(String categoria);

    /**
     * Verificar si existe un producto con el mismo nombre (para validaciones)
     */
    boolean existsByNombreIgnoreCase(String nombre);
}