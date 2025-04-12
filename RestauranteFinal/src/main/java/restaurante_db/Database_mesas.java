package restaurante_db;

import restaurante_gestion_de_mesas_y_comandas.Mesa;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Database_mesas {

    // Lista que almacena las mesas
    //private static final List<Mesa> mesas = new ArrayList<>();
    private static final String ARCHIVO_MESAS = "src/resources/archivos/mesas.txt";  // Ruta del archivo
    private static LinkedList<Mesa> mesas = new LinkedList<>();
    private static List<Mesa> listaMesas = new ArrayList<>();



    static {
        cargarMesasDesdeArchivo();  // Cargar las mesas cuando la clase sea inicializada
    }

    // Método para obtener la lista de todas las mesas
    public static List<Mesa> getMesas() {
        return new ArrayList<>(mesas);  // Retorna una copia para evitar modificaciones directas
    }

    // Método para obtener una mesa por su número
    public static Mesa getMesaPorNumero(int numeroMesa) {
        for (Mesa mesa : mesas) {
            if (mesa.getNumeroMesa() == numeroMesa) {
                return mesa;
            }
        }
        return null; // Si no se encuentra la mesa
    }

    // Método para agregar mesas a la lista (ejemplo)
    public static void agregarMesa(Mesa mesa) {
        listaMesas.add(mesa);
    }

    // Método para obtener todas las mesas
    public static List<Mesa> obtenerListaDeMesas() {
        return listaMesas;
    }

    // Método para actualizar una mesa
    public static void actualizarMesa(Mesa mesa) {
        for (int i = 0; i < mesas.size(); i++) {
            if (mesas.get(i).getNumeroMesa() == mesa.getNumeroMesa()) {
                mesas.set(i, mesa); // Actualiza la mesa en la lista
                guardarMesasEnArchivo();  // Guardar los cambios en el archivo
                break;
            }
        }
    }

    // Método para eliminar una mesa
    public static void eliminarMesa(int numeroMesa) {
        mesas.removeIf(mesa -> mesa.getNumeroMesa() == numeroMesa);
        guardarMesasEnArchivo();  // Guardar los cambios en el archivo
    }

    // Cargar las mesas desde el archivo al iniciar la aplicación
    private static void cargarMesasDesdeArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_MESAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datosMesa = linea.split(",");
                int numeroMesa = Integer.parseInt(datosMesa[0].trim());
                String estado = datosMesa[1].trim();
                Mesa mesa = new Mesa(numeroMesa);
                mesas.add(mesa);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar las mesas desde el archivo: " + e.getMessage());
        }
    }

    // Guardar las mesas actuales en el archivo
    private static void guardarMesasEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_MESAS))) {
            for (Mesa mesa : mesas) {
                writer.write(mesa.getNumeroMesa() + "," + mesa.getEstado());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar las mesas en el archivo: " + e.getMessage());
        }
    }
}
