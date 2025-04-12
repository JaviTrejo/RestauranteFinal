package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import restaurante_db.Database_mesas;
import restaurante_gestion_de_mesas_y_comandas.Comanda;
import restaurante_gestion_de_mesas_y_comandas.Mesa;
import restaurante_gestion_de_mesas_y_comandas.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Comanda_controller {

    private static final String RUTA_COMANDAS = "comandas.txt";

    // Observable list para las comandas
    private final ObservableList<Comanda> comandasObservable;
    private final ComboBox<Comanda> comboComandas;
    private ComboBox<Mesa> comboBoxMesas;
    private Mesa_controller mesaController;



    private final Stage primaryStage;

    // Constructor
    public Comanda_controller(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label titleLabel = new Label("=== COMANDAS ===");
        List<Comanda> comandas = cargarComandas();

        comandasObservable = FXCollections.observableArrayList(comandas); // Lista observable para las comandas
        comboComandas = new ComboBox<>(comandasObservable);

        comboComandas.setCellFactory(param -> new ListCell<Comanda>() {
            @Override
            protected void updateItem(Comanda item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Mesa #" + item.getNumeroMesa());
                }
            }
        });

        // Inicializar comboBoxMesas con mesas libres
        comboBoxMesas = new ComboBox<>();
        for (Mesa mesa : Database_mesas.obtenerListaDeMesas()) {
            if (mesa.getEstado().equalsIgnoreCase("libre")) {
                comboBoxMesas.getItems().add(mesa);
            }
        }
        comboBoxMesas.setPromptText("Selecciona una mesa");

        Button agregarComandaButton = new Button("Agregar Comanda");
        agregarComandaButton.setOnAction(e -> agregarComanda());

        Button cerrarComandaButton = new Button("Cerrar Comanda");
        cerrarComandaButton.setOnAction(e -> cerrarComanda());

        // Agregar los componentes a la ventana
        root.getChildren().addAll(titleLabel, comboComandas, comboBoxMesas, agregarComandaButton, cerrarComandaButton);

        // Crear la escena
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestión de Comandas");
    }


    // Método start para inicializar la ventana
    public void start() {
        primaryStage.show(); // Mostrar la ventana
    }

    // Cargar las comandas desde el archivo
    private List<Comanda> cargarComandas() {
        List<Comanda> comandas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_COMANDAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Comanda comanda = procesarLineaComanda(linea);
                if (comanda != null) {
                    comandas.add(comanda);
                }
            }
        } catch (IOException e) {
            ManejadorErrores.mostrarErrorGenerico("No se pudo cargar el archivo de comandas. Intenta nuevamente.");
        }

        if (comandas.isEmpty()) {
            ManejadorErrores.mostrarAdvertencia("No hay comandas disponibles.");
        }

        return comandas;
    }

    // Procesar una línea del archivo de comandas y devolver la comanda correspondiente
    private Comanda procesarLineaComanda(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length != 4) {
            return null;
        }

        try {
            int numeroComanda = Integer.parseInt(partes[0]);
            int numeroMesa = Integer.parseInt(partes[1]);
            List<Producto> listaProductos = procesarProductos(partes[2]);
            Mesa mesa = new Mesa(numeroMesa);
            return new Comanda(numeroComanda, mesa, listaProductos);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Procesar los productos de la comanda
    private List<Producto> procesarProductos(String productosString) {
        List<Producto> listaProductos = new ArrayList<>();
        String[] productosDatos = productosString.split(",");

        for (int i = 0; i < productosDatos.length; i += 3) {
            if (i + 2 >= productosDatos.length) {
                continue;
            }
            Producto producto = crearProducto(Arrays.copyOfRange(productosDatos, i, i + 3));
            listaProductos.add(producto);
        }

        return listaProductos;
    }

    // Crear un producto a partir de sus datos
    private Producto crearProducto(String[] datosProducto) {
        int idProducto = Integer.parseInt(datosProducto[0]);
        String nombreProducto = datosProducto[1];
        double precioProducto = Double.parseDouble(datosProducto[2]);
        return new Producto(idProducto, nombreProducto, precioProducto);
    }

    // Agregar una nueva comanda
    private void agregarComanda() {
        Mesa mesaSeleccionada = comboBoxMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada != null) {
            Comanda nuevaComanda = new Comanda(GeneradorIdComanda.generarId(), mesaSeleccionada, new ArrayList<>());
            // Guardar comanda y realizar otras acciones necesarias
        }
    }

    // Método para seleccionar una mesa (puede ser un combo box o una lista)
    private Mesa seleccionarMesa() {
        // Lógica para seleccionar una mesa, se puede usar un ComboBox o un método similar
        return new Mesa(1); // Ejemplo con mesa 1
    }

    // Guardar la nueva comanda en el archivo
    private void guardarComanda(Comanda comanda) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_COMANDAS, true))) {
            writer.write(comanda.toArchivoString());
            writer.newLine();
        } catch (IOException e) {
            ManejadorErrores.mostrarErrorGenerico("No se pudo guardar la nueva comanda.");
        }
    }

    // Cerrar una comanda (liberar la mesa y eliminar la comanda)
    private void cerrarComanda() {
        Comanda comandaSeleccionada = comboComandas.getSelectionModel().getSelectedItem();
        if (comandaSeleccionada != null) {
            comandaSeleccionada.getMesa().liberarMesa();
            eliminarComandaDelArchivo(comandaSeleccionada);
            comandasObservable.remove(comandaSeleccionada);
        }
    }

    // Eliminar una comanda del archivo
    private void eliminarComandaDelArchivo(Comanda comandaAEliminar) {
        List<Comanda> todasComandas = cargarComandas();
        todasComandas.removeIf(comanda -> comanda.getNumeroComanda() == comandaAEliminar.getNumeroComanda());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_COMANDAS))) {
            for (Comanda comanda : todasComandas) {
                writer.write(comanda.toArchivoString());
                writer.newLine();
            }
        } catch (IOException e) {
            ManejadorErrores.mostrarErrorGenerico("No se pudo eliminar la comanda.");
        }
    }

    // Generador de ID para las comandas (puedes implementarlo según tu lógica)
    private static class GeneradorIdComanda {
        private static int id = 1;

        public static int generarId() {
            return id++;
        }
    }
}
