package restaurante_gestion_de_mesas_y_comandas;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Comanda {
    private final IntegerProperty numeroMesa;
    private final IntegerProperty numeroComanda;
    private final ListProperty<Producto> productos;
    private final BooleanProperty cerrada;
    private final Mesa mesa;

    // Constructor
    public Comanda(int numeroComanda, Mesa mesa, List<Producto> productos) {
        this.numeroComanda = new SimpleIntegerProperty(numeroComanda);
        this.mesa = mesa;
        this.numeroMesa = new SimpleIntegerProperty(mesa.getNumeroMesa());
        this.productos = new SimpleListProperty<>(FXCollections.observableArrayList(productos));
        this.cerrada = new SimpleBooleanProperty(false);
    }

    // Getter y Setter
    public int getNumeroMesa() {
        return numeroMesa.get();
    }

    public IntegerProperty numeroMesaProperty() {
        return numeroMesa;
    }

    public int getNumeroComanda() {
        return numeroComanda.get();
    }

    public IntegerProperty numeroComandaProperty() {
        return numeroComanda;
    }

    public ObservableList<Producto> getProductos() {
        return productos.get();
    }

    public ListProperty<Producto> productosProperty() {
        return productos;
    }

    public boolean isCerrada() {
        return cerrada.get();
    }

    public BooleanProperty cerradaProperty() {
        return cerrada;
    }

    public Mesa getMesa() {
        return mesa;
    }

    // Cargar comandas desde archivo
    public static List<Comanda> cargarComandasDesdeArchivo() {
        List<Comanda> comandas = new ArrayList<>();
        File archivo = new File("comandas.txt");

        if (!archivo.exists()) return comandas;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");

                if (partes.length < 4) continue;

                int numeroComanda = Integer.parseInt(partes[0]);
                int numeroMesa = Integer.parseInt(partes[1]);

                String[] productosString = partes[2].split(",");
                List<Producto> productos = new ArrayList<>();

                for (int i = 0; i < productosString.length; i += 3) {
                    int id = Integer.parseInt(productosString[i]);
                    String nombre = productosString[i + 1];
                    double precio = Double.parseDouble(productosString[i + 2]);
                    productos.add(new Producto(id, nombre, precio));
                }

                Mesa mesa = new Mesa(numeroMesa);
                Comanda comanda = new Comanda(numeroComanda, mesa, productos);

                // Marcar si está cerrada
                if (partes[3].equalsIgnoreCase("cerrada")) {
                    comanda.cerrarComanda();
                }

                comandas.add(comanda);
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al leer las comandas desde el archivo: " + e.getMessage());
        }

        return comandas;
    }

    // Agregar o modificar producto en la comanda
    public void agregarProducto(Producto producto, int cantidad) {
        if (!cerrada.get()) {
            boolean productoExistente = false;

            // Comprobar si el producto ya existe en la comanda
            for (Producto p : productos) {
                if (p.getId() == producto.getId()) {  // Si el ID es el mismo, ya existe el producto
                    p.incrementarCantidad(cantidad);  // Aumentamos la cantidad
                    productoExistente = true;
                    break;
                }
            }

            // Si el producto no existía, lo añadimos
            if (!productoExistente) {
                for (int i = 0; i < cantidad; i++) {
                    productos.add(producto);  // Añadimos el producto
                }
            }
            guardarComandaEnArchivo();  // Guardar los cambios
        } else {
            System.out.println("¡La comanda está cerrada!");
        }
    }

    // Eliminar un producto de la comanda
    public void eliminarProducto(String nombreProducto) {
        if (!cerrada.get()) {
            productos.removeIf(producto -> producto.getNombre().equalsIgnoreCase(nombreProducto));
            guardarComandaEnArchivo();
        } else {
            System.out.println("¡La comanda está cerrada!");
        }
    }

    // Mostrar la comanda en pantalla
    public void mostrarComanda() {
        System.out.println("Comanda #" + numeroComanda + " | Mesa: " + numeroMesa);
        if (productos.isEmpty()) {
            System.out.println("Sin productos.");
        } else {
            for (Producto p : productos) {
                System.out.println("- " + p.getNombre() + " | Precio: $" + p.getPrecio() + " | Cantidad: " + p.getCantidad());
            }
        }
        System.out.println("Total: $" + calcularTotal());
    }

    // Calcular el total de la comanda
    public double calcularTotal() {
        double total = 0;
        for (Producto p : productos) {
            total += p.calcularTotal();  // Total de cada producto (precio * cantidad)
        }
        return total;
    }

    // Cerrar la comanda
    public void cerrarComanda() {
        cerrada.set(true);
        guardarComandaEnArchivo();
    }

    // Guardar la comanda en un archivo de texto
    private void guardarComandaEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("comandas.txt", true))) {
            writer.write(this.toArchivoString() + "\n");
        } catch (IOException e) {
            System.out.println("Error al guardar la comanda en el archivo.");
        }
    }

    // Método que convierte la comanda a un string para guardar en el archivo
    public String toArchivoString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNumeroComanda()).append("|")
                .append(getNumeroMesa()).append("|");

        for (Producto p : productos) {
            sb.append(p.getId()).append(",")
                    .append(p.getNombre()).append(",")
                    .append(p.getPrecio()).append(",");
        }

        if (!productos.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1); // Quitar la última coma
        }

        sb.append("|").append(isCerrada() ? "cerrada" : "abierta");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Comanda #" + numeroComanda + " de Mesa " + numeroMesa +
                " | Productos: " + productos.size() +
                " | Cerrada: " + cerrada;
    }
}
