package co.edu.uptc.controller;

import co.edu.uptc.model.Producto;
import co.edu.uptc.service.ProductoService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controlador que maneja la interfaz de usuario por consola
 * Presenta menús y coordina las operaciones CRUD
 */
public class ProductoController {
    private ProductoService service;
    private Scanner scanner;

    public ProductoController() {
        this.service = new ProductoService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Método principal que inicia el menú
     */
    public void iniciarMenu() {
        mostrarBienvenida();

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();

            try {
                switch (opcion) {
                    case 1 -> crearNuevoProducto();
                    case 2 -> listarTodosLosProductos();
                    case 3 -> buscarProductoPorId();
                    case 4 -> actualizarProducto();
                    case 5 -> eliminarProducto();
                    case 6 -> buscarProductoPorNombre();
                    case 0 -> {
                        mostrarDespedida();
                        continuar = false;
                    }
                    default -> System.out.println("Opcion invalida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (continuar) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
    }

    private void mostrarBienvenida() {
        System.out.println("=".repeat(50));
        System.out.println("  SISTEMA DE GESTION DE PRODUCTOS");
        System.out.println(" CRUD Completo - Parte 1 (Sin Seguridad)");
        System.out.println("=".repeat(50));
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("MENU PRINCIPAL");
        System.out.println("=".repeat(40));
        System.out.println("1. Crear producto");
        System.out.println("2. Listar todos los productos");
        System.out.println("3. Buscar producto por ID");
        System.out.println("4. Actualizar producto");
        System.out.println("5. Eliminar producto");
        System.out.println("6. Buscar por nombre");
        System.out.println("0. Salir");
        System.out.println("=".repeat(40));
        System.out.print("Seleccione una opcion: ");
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * CREAR - Permite crear un nuevo producto
     */
    private void crearNuevoProducto() {
        System.out.println("\nCREAR NUEVO PRODUCTO");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();

            System.out.print("Precio: ");
            Double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Stock: ");
            Integer stock = Integer.parseInt(scanner.nextLine());

            System.out.print("Categoría: ");
            String categoria = scanner.nextLine();

            Producto producto = new Producto(null, nombre, descripcion, precio, stock, categoria);
            Producto productoCreado = service.crearProducto(producto);

            System.out.println("\nProducto creado exitosamente:");
            mostrarProducto(productoCreado);

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese numeros validos para precio y stock");
        }
    }

    /**
     * LEER - Muestra todos los productos
     */
    private void listarTodosLosProductos() {
        System.out.println("\nLISTA DE PRODUCTOS");
        System.out.println("-".repeat(30));

        List<Producto> productos = service.obtenerTodosLosProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
        } else {
            System.out.printf("%-5s %-20s %-10s %-8s %-15s%n",
                    "ID", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA");
            System.out.println("-".repeat(60));

            for (Producto producto : productos) {
                System.out.printf("%-5d %-20s $%-9.2f %-8d %-15s%n",
                        producto.getId(),
                        producto.getNombre().length() > 20 ? producto.getNombre().substring(0, 17) + "..."
                                : producto.getNombre(),
                        producto.getPrecio(),
                        producto.getStock(),
                        producto.getCategoria() != null ? producto.getCategoria() : "N/A");
            }
        }
    }

    /**
     * LEER - Busca un producto por ID
     */
    private void buscarProductoPorId() {
        System.out.println("\nBUSCAR PRODUCTO POR ID");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Ingrese el ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Producto> producto = service.buscarProductoPorId(id);

            if (producto.isPresent()) {
                System.out.println("Producto encontrado:");
                mostrarProducto(producto.get());
            } else {
                System.out.println("❌ Producto no encontrado con ID: " + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID válido");
        }
    }

    /**
     * ACTUALIZAR - Modifica un producto existente
     */
    private void actualizarProducto() {
        System.out.println("\nACTUALIZAR PRODUCTO");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Ingrese el ID del producto a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Producto> productoExistente = service.buscarProductoPorId(id);
            if (productoExistente.isEmpty()) {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                return;
            }

            System.out.println("Producto actual:");
            mostrarProducto(productoExistente.get());

            System.out.println("\nIngrese los nuevos datos:");

            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Nueva descripción: ");
            String descripcion = scanner.nextLine();

            System.out.print("Nuevo precio: ");
            Double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Nuevo stock: ");
            Integer stock = Integer.parseInt(scanner.nextLine());

            System.out.print("Nueva categoría: ");
            String categoria = scanner.nextLine();

            Producto productoActualizado = new Producto(id, nombre, descripcion, precio, stock, categoria);
            Producto resultado = service.actualizarProducto(id, productoActualizado);

            System.out.println("\nProducto actualizado exitosamente:");
            mostrarProducto(resultado);

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores validos");
        }
    }

    /**
     * ELIMINAR - Borra un producto
     */
    private void eliminarProducto() {
        System.out.println("\nELIMINAR PRODUCTO");
        System.out.println("-".repeat(30));

        try {
            System.out.print("Ingrese el ID del producto a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<Producto> producto = service.buscarProductoPorId(id);
            if (producto.isEmpty()) {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                return;
            }

            System.out.println("Producto a eliminar:");
            mostrarProducto(producto.get());

            System.out.print("¿Está seguro? (s/N): ");
            String confirmacion = scanner.nextLine().toLowerCase();

            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                boolean eliminado = service.eliminarProducto(id);
                if (eliminado) {
                    System.out.println("Producto eliminado exitosamente");
                }
            } else {
                System.out.println("Eliminacion cancelada");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese un ID válido");
        }
    }

    /**
     * BUSCAR - Busca productos por nombre
     */
    private void buscarProductoPorNombre() {
        System.out.println("\nBUSCAR POR NOMBRE");
        System.out.println("-".repeat(30));

        System.out.print("Ingrese el nombre a buscar: ");
        String nombre = scanner.nextLine();

        List<Producto> productos = service.buscarPorNombre(nombre);

        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos con: " + nombre);
        } else {
            System.out.println("Productos encontrados:");
            for (Producto producto : productos) {
                mostrarProducto(producto);
                System.out.println();
            }
        }
    }

    private void mostrarProducto(Producto producto) {
        System.out.println("┌" + "-".repeat(40) + "┐");
        System.out.println("│ ID: " + producto.getId());
        System.out.println("│ Nombre: " + producto.getNombre());
        System.out.println("│ Descripción: " + (producto.getDescripcion() != null ? producto.getDescripcion() : "N/A"));
        System.out.println("│ Precio: $" + producto.getPrecio());
        System.out.println("│ Stock: " + producto.getStock());
        System.out.println("│ Categoría: " + (producto.getCategoria() != null ? producto.getCategoria() : "N/A"));
        System.out.println("│ Fecha: " + producto.getFechaCreacion());
        System.out.println("└" + "-".repeat(40) + "┘");
    }

    private void mostrarDespedida() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(" Gracias por usar el sistema!");
        System.out.println(" CRUD completado sin seguridad");
        System.out.println("=".repeat(50));
    }
}