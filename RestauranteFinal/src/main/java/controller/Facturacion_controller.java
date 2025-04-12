package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import restaurante_gestion_de_mesas_y_comandas.Comanda;
import restaurante_gestion_de_mesas_y_comandas.Mesa;
import restaurante_gestion_de_mesas_y_comandas.Producto;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Facturacion_controller {

    public static double ventasTotalesDelDia = 0;
    public static int cantidadVentasDelDia = 0;

    private static final Logger logger = Logger.getLogger(Facturacion_controller.class.getName());
    private static final String RUTA_COMANDAS = "comandas.txt";

    // Declarar los RadioButtons como miembros de la clase
    private RadioButton efectivoRadio;
    private RadioButton tarjetaRadio;
    private RadioButton transferenciaRadio;

    // Hacer que comboComandas sea una variable miembro de la clase
    private ComboBox<Comanda> comboComandas;

    // Observable list para las comandas
    private ObservableList<Comanda> comandasObservable;

    private double ventasDelDia;
    private int cantidadVentas;

    // Constructor
    public Facturacion_controller() {
        this.ventasDelDia = 0.0;  // Inicialización de ventas del día
        this.cantidadVentas = 0;  // Inicialización de cantidad de ventas
    }

    // Método para registrar una venta
    public void registrarVenta(double montoVenta) {
        this.ventasDelDia += montoVenta;  // Sumar la venta al total del día
        this.cantidadVentas++;  // Incrementar la cantidad de ventas realizadas
        actualizarArchivoVentas();  // Guardar los datos actualizados
    }

    // Método para obtener las ventas del día
    public double obtenerVentasDelDia() {
        return ventasDelDia;
    }

    // Método para obtener la cantidad de ventas
    public int obtenerCantidadVentas() {
        return cantidadVentas;
    }

    // Método para guardar las ventas en un archivo (por ejemplo, "ventas.txt")
    private void actualizarArchivoVentas() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ventas.txt"))) {
            writer.write("Ventas del día: " + ventasDelDia + "\n");
            writer.write("Cantidad de ventas: " + cantidadVentas + "\n");
        } catch (IOException e) {
            System.out.println("Error al guardar las ventas: " + e.getMessage());
        }
    }

    // Método para cargar las ventas desde el archivo
    public void cargarVentasDesdeArchivo() {
        File archivo = new File("ventas.txt");
        if (archivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (linea.startsWith("Ventas del día: ")) {
                        this.ventasDelDia = Double.parseDouble(linea.split(": ")[1]);
                    }
                    if (linea.startsWith("Cantidad de ventas: ")) {
                        this.cantidadVentas = Integer.parseInt(linea.split(": ")[1]);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error al cargar las ventas: " + e.getMessage());
            }
        }
    }


    // Cargar comandas desde el archivo
    private static List<Comanda> cargarComandas() {
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
            logger.severe("Error al cargar las comandas: " + e.getMessage());
            ManejadorErrores.mostrarErrorGenerico("No se pudo cargar el archivo de comandas. Intenta nuevamente.");
        }

        if (comandas.isEmpty()) {
            ManejadorErrores.mostrarAdvertencia("No hay comandas disponibles.");
        }

        return comandas;
    }

    // Método para procesar la línea y devolver la comanda con sus productos
    private static Comanda procesarLineaComanda(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length != 4) {
            logger.warning("Línea mal formateada: " + linea);
            return null;
        }

        try {
            int numeroComanda = Integer.parseInt(partes[0]);
            int numeroMesa = Integer.parseInt(partes[1]);

            List<Producto> listaProductos = procesarProductos(partes[2]);
            if (listaProductos.isEmpty()) {
                logger.warning("No se encontraron productos válidos en la comanda: " + linea);
                return null;
            }

            Mesa mesa = new Mesa(numeroMesa);
            return new Comanda(numeroComanda, mesa, listaProductos);
        } catch (NumberFormatException e) {
            logger.warning("Error al analizar la comanda: " + e.getMessage());
            return null;
        }
    }

    // Método para procesar los productos desde una cadena
    private static List<Producto> procesarProductos(String productosString) {
        List<Producto> listaProductos = new ArrayList<>();
        String[] productosDatos = productosString.split(",");

        for (int i = 0; i < productosDatos.length; i += 3) {
            if (i + 2 >= productosDatos.length) {
                logger.warning("Producto incompleto, se omite: " + Arrays.toString(
                        Arrays.copyOfRange(productosDatos, i, productosDatos.length)));
                continue;
            }

            try {
                Producto producto = crearProducto(Arrays.copyOfRange(productosDatos, i, i + 3));
                listaProductos.add(producto);
            } catch (NumberFormatException e) {
                logger.warning("Error al crear producto: " + e.getMessage());
            }

        }

        return listaProductos;
    }

    // Método para crear un Producto a partir de 3 datos
    private static Producto crearProducto(String[] datosProducto) {
        int idProducto = Integer.parseInt(datosProducto[0]);
        String nombreProducto = datosProducto[1];
        double precioProducto = Double.parseDouble(datosProducto[2]);
        return new Producto(idProducto, nombreProducto, precioProducto);
    }

    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Label titleLabel = new Label("=== FACTURACIÓN ===");
        List<Comanda> comandas = cargarComandas();

        comandasObservable = FXCollections.observableArrayList(comandas);  // Lista observable para las comandas
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

        TextArea comandaText = new TextArea();
        comandaText.setEditable(false);
        Label totalLabel = new Label("Total: $0.00");

        comboComandas.setOnAction(e -> {
            Comanda selectedComanda = comboComandas.getSelectionModel().getSelectedItem();
            if (selectedComanda != null) {
                StringBuilder productos = new StringBuilder();
                for (Producto producto : selectedComanda.getProductos()) {
                    productos.append(producto.getNombre()).append(" - $").append(producto.getPrecio()).append("\n");
                }
                comandaText.setText(productos.toString());
                double total = selectedComanda.calcularTotal();
                totalLabel.setText("Total: $" + total);
            }
        });

        Label metodoPagoLabel = new Label("Métodos de pago disponibles:");
        efectivoRadio = new RadioButton("Efectivo");
        tarjetaRadio = new RadioButton("Tarjeta de crédito");
        transferenciaRadio = new RadioButton("Transferencia");

        ToggleGroup paymentGroup = new ToggleGroup();
        efectivoRadio.setToggleGroup(paymentGroup);
        tarjetaRadio.setToggleGroup(paymentGroup);
        transferenciaRadio.setToggleGroup(paymentGroup);

        VBox paymentBox = new VBox(5, efectivoRadio, tarjetaRadio, transferenciaRadio);

        // Boton para imprimir comanda
        Button imprimirButton = new Button("Imprimir Comanda");
        imprimirButton.setOnAction(e -> {
            Comanda selectedComanda = comboComandas.getSelectionModel().getSelectedItem();
            if (selectedComanda != null && !selectedComanda.getProductos().isEmpty()) {
                imprimirComanda(selectedComanda);  // Llamar al método para imprimir la comanda
            } else {
                ManejadorErrores.mostrarAdvertencia("La comanda seleccionada no tiene productos.");
            }
        });

        // Agregar el botón a la interfaz de usuario
        root.getChildren().add(imprimirButton);


        Button facturarButton = new Button("Procesar Pago");

        facturarButton.setOnAction(e -> {
            Comanda selectedComanda = comboComandas.getSelectionModel().getSelectedItem();
            if (selectedComanda != null && !selectedComanda.getProductos().isEmpty()) {  // Verifica si la comanda tiene productos
                String metodo = obtenerMetodoPago(paymentGroup);

                if (metodo == null) {
                    ManejadorErrores.mostrarAdvertencia("Por favor, seleccione un método de pago.");
                    return;
                }

                double total = selectedComanda.calcularTotal();
                mostrarConfirmacionPago(metodo, total);

                ventasTotalesDelDia += total;
                cantidadVentasDelDia++;

                // Cerrar la comanda y liberar la mesa
                selectedComanda.getMesa().liberarMesa();

                // Eliminar comanda del archivo
                eliminarComandaDelArchivo(selectedComanda);

                // Eliminar comanda de la lista observable
                comandasObservable.remove(selectedComanda);
            } else {
                ManejadorErrores.mostrarAdvertencia("La comanda seleccionada no tiene productos.");
            }
        });

        // Crear la escena con el contenedor root
        Scene scene = new Scene(root, 500, 400);

        // Comprobar y agregar el CSS
        URL cssUrl = getClass().getResource("/resources/Styles.css");
        if (cssUrl == null) {
            logger.warning("El archivo CSS no se pudo cargar.");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Agregar todos los elementos a la ventana principal
        root.getChildren().addAll(titleLabel, comboComandas, comandaText, metodoPagoLabel, paymentBox, facturarButton);

        // Configurar y mostrar la ventana principal
        primaryStage.setTitle("Facturación");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String obtenerMetodoPago(ToggleGroup paymentGroup) {
        if (efectivoRadio.isSelected()) {
            return "Efectivo";
        } else if (tarjetaRadio.isSelected()) {
            return "Tarjeta de crédito";
        } else if (transferenciaRadio.isSelected()) {
            return "Transferencia";
        }
        return null;
    }

    private void mostrarConfirmacionPago(String metodo, double total) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pago procesado");
        alert.setHeaderText(null);
        alert.setContentText("✅ Pago procesado exitosamente.\nMétodo de pago: " + metodo + "\nTotal pagado: $" + total);
        alert.showAndWait();
    }

    private void eliminarComandaDelArchivo(Comanda comandaAEliminar) {
        List<Comanda> todas = cargarComandas();
        todas.removeIf(c -> c.getNumeroComanda() == comandaAEliminar.getNumeroComanda());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_COMANDAS, false))) {
            for (Comanda c : todas) {
                writer.write(c.toArchivoString()); // Método que debes agregar en la clase Comanda
                writer.newLine();
            }
        } catch (IOException e) {
            logger.severe("Error al actualizar el archivo de comandas: " + e.getMessage());
        }
    }

    private void imprimirComanda(Comanda comanda) {
        // Comprobar si hay una impresora predeterminada
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            ManejadorErrores.mostrarAdvertencia("No se encontró una impresora predeterminada.");
            return;  // Salir del método si no hay impresora
        }

        // Crear un objeto Text que contendrá el contenido a imprimir
        StringBuilder contenido = new StringBuilder();
        contenido.append("Comanda #: ").append(comanda.getNumeroComanda()).append("\n");
        contenido.append("Mesa #: ").append(comanda.getMesa().getNumeroMesa()).append("\n");
        contenido.append("Productos:\n");

        // Añadir los productos a la lista de impresión
        for (Producto producto : comanda.getProductos()) {
            contenido.append("- ").append(producto.getNombre())
                    .append(" - $").append(producto.getPrecio()).append("\n");
        }

        contenido.append("Total: $").append(comanda.calcularTotal()).append("\n");

        // Crear un objeto Text con el contenido que deseas imprimir
        Text textoParaImprimir = new Text(contenido.toString());
        textoParaImprimir.setFont(Font.font(12));  // Establecer el tamaño de la fuente

        // Crear un PrinterJob (trabajo de impresión)
        PrinterJob printerJob = PrinterJob.createPrinterJob(printer);

        // Mostrar el diálogo de impresión y verificar si el usuario seleccionó la impresora
        if (printerJob.showPrintDialog(null)) {  // Mostrar el diálogo de impresión
            // Imprimir directamente con la impresora seleccionada
            boolean success = printerJob.printPage(textoParaImprimir);
            if (success) {
                printerJob.endJob();  // Finalizar el trabajo de impresión

                // Llamar a ManejadorErrores para mostrar la confirmación de impresión
                ManejadorErrores.mostrarConfirmacionImpresion();
            } else {
                ManejadorErrores.mostrarErrorGenerico("Error al imprimir la comanda.");
            }
        } else {
            ManejadorErrores.mostrarAdvertencia("No se seleccionó una impresora.");
        }
    }

}
