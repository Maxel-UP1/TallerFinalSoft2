package co.edu.uptc.repository;

import co.edu.uptc.model.Producto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para manejar la persistencia de productos en JSON
 * Esta clase implementa todas las operaciones CRUD básicas
 */
public class ProductoRepository {
    private Gson gson;
    private static final String ARCHIVO_PRODUCTOS = "data/productos.json";

    public ProductoRepository() {
        // Configurar Gson para manejar fechas LocalDate
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> context
                                .serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE))
                .create();
    }

    /**
     * CREAR - Guarda un nuevo producto
     */
    public Producto crear(Producto producto) {
        List<Producto> productos = obtenerTodos();

        // Generar nuevo ID automáticamente
        Long nuevoId = productos.isEmpty() ? 1L : productos.stream().mapToLong(Producto::getId).max().orElse(0L) + 1;
        producto.setId(nuevoId);

        productos.add(producto);
        guardarArchivo(productos);

        System.out.println("✅ Producto creado con ID: " + nuevoId);
        return producto;
    }

    /**
     * LEER - Obtiene todos los productos
     */
    public List<Producto> obtenerTodos() {
        try {
            File archivo = new File(ARCHIVO_PRODUCTOS);
            if (!archivo.exists()) {
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(archivo)) {
                Type listType = new TypeToken<List<Producto>>() {
                }.getType();
                List<Producto> productos = gson.fromJson(reader, listType);
                return productos != null ? productos : new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * LEER - Busca un producto por ID
     */
    public Optional<Producto> buscarPorId(Long id) {
        return obtenerTodos().stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst();
    }

    /**
     * ACTUALIZAR - Modifica un producto existente
     */
    public Producto actualizar(Producto productoActualizado) {
        List<Producto> productos = obtenerTodos();

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(productoActualizado.getId())) {
                productos.set(i, productoActualizado);
                guardarArchivo(productos);
                System.out.println("✅ Producto actualizado con ID: " + productoActualizado.getId());
                return productoActualizado;
            }
        }

        throw new RuntimeException("Producto no encontrado con ID: " + productoActualizado.getId());
    }

    /**
     * ELIMINAR - Borra un producto por ID
     */
    public boolean eliminar(Long id) {
        List<Producto> productos = obtenerTodos();
        boolean eliminado = productos.removeIf(producto -> producto.getId().equals(id));

        if (eliminado) {
            guardarArchivo(productos);
            System.out.println("✅ Producto eliminado con ID: " + id);
        }

        return eliminado;
    }

    /**
     * Buscar productos por nombre (contiene)
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return obtenerTodos().stream()
                .filter(producto -> producto.getNombre().toLowerCase()
                        .contains(nombre.toLowerCase()))
                .toList();
    }

    /**
     * Método privado para guardar la lista en el archivo JSON
     */
    private void guardarArchivo(List<Producto> productos) {
        try {
            // Crear directorio si no existe
            File archivo = new File(ARCHIVO_PRODUCTOS);
            archivo.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(archivo)) {
                gson.toJson(productos, writer);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}