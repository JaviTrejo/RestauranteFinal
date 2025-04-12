package restaurante_db;

import restaurante_gestion_de_mesas_y_comandas.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database_comida {
    private static final String FILE_PATH = "productos.txt";  // Archivo donde se almacenan los productos del menú
    private static final List<Producto> menuProductos = new ArrayList<>();  // Lista de productos en el menú

    // Cargar productos al iniciar la aplicación
    static {
        cargarProductosDesdeArchivo();
    }

    // Obtener la lista de productos en el menú
    public static List<Producto> obtenerMenu() {
        return new ArrayList<>(menuProductos); // Retorna una copia para evitar modificaciones externas
    }

    // Agregar un nuevo producto al menú
    public static boolean agregarProducto(String nombre, double precio) {
        if (nombre == null || nombre.trim().isEmpty() || precio < 0) {
            return false; // Validación de datos incorrectos
        }

        Producto productoExistente = obtenerProductoPorNombre(nombre);
        if (productoExistente != null) {
            return false; // Si el producto ya existe, no se agrega nuevamente
        }

        // Asignar un ID único al nuevo producto
        int id = menuProductos.isEmpty() ? 1 : menuProductos.getLast().getId() + 1;
        Producto nuevoProducto = new Producto(id, nombre, precio);
        menuProductos.add(nuevoProducto);
        return guardarProductosEnArchivo();
    }

    // Eliminar un producto del menú
    public static boolean eliminarProducto(int id) {
        boolean eliminado = menuProductos.removeIf(producto -> producto.getId() == id);
        return eliminado && guardarProductosEnArchivo();
    }

    // Buscar un producto por su nombre
    public static Producto obtenerProductoPorNombre(String nombre) {
        return menuProductos.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    // Buscar un producto por su ID
    public static Producto obtenerProductoPorId(int id) {
        return menuProductos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Guardar productos en el archivo
    public static boolean guardarProductosEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Producto p : menuProductos) {
                writer.write(p.getId() + "," + p.getNombre() + "," + p.getPrecio());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
            return false;
        }
    }

    // Cargar productos desde el archivo
    private static void cargarProductosDesdeArchivo() {
        menuProductos.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String nombre = parts[1];
                    double precio = Double.parseDouble(parts[2]);
                    menuProductos.add(new Producto(id, nombre, precio));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }
}
