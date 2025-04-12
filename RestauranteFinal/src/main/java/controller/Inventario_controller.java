package controller;

import generador.Correo;
import generador.GeneradorPDF;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.restaurantefinal.restaurantefinal.Usuario;
import restaurante_gestion_de_mesas_y_comandas.Producto_inventario;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Inventario_controller {

    private static final Map<String, Producto_inventario> inventarioMap = new HashMap<>();

    // Método para mostrar inventario en la interfaz gráfica
    public static void mostrarInventario(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Botones para las acciones
        Button agregarProductoButton = new Button("Agregar Producto");
        agregarProductoButton.setOnAction(e -> abrirFormularioAgregarProducto(stage));

        Button eliminarProductoButton = new Button("Eliminar Producto");
        eliminarProductoButton.setOnAction(e -> abrirFormularioEliminarProducto(stage));

        Button enviarPorCorreoButton = new Button("Enviar Inventario por Correo");
        enviarPorCorreoButton.setOnAction(e -> {
            String rutaInventario = "src/main/java/resources/archivos/Inventario.txt"; // Ruta del archivo Inventario.txt
            String rutaPDF = "src/main/java/resources/archivos/Inventario.pdf"; // Ruta para el PDF

            GeneradorPDF.crearPDFInventarioDesdeArchivo(rutaInventario, rutaPDF);
            Correo.enviarEmailConAdjunto("Inventario");
        });

        // Botón para ver el inventario en PDF
        Button verPDFButton = new Button("Ver Inventario en PDF");
        verPDFButton.setOnAction(e -> mostrarPDF());

        // Botón para volver
        Button volverButton = new Button("Volver");
        volverButton.setOnAction(e -> {
            Inventario_controller controller = new Inventario_controller();
            controller.volverAlMenu(stage);  // Invoca el método a través de la instancia
        });

        layout.getChildren().addAll(
                agregarProductoButton,
                eliminarProductoButton,
                enviarPorCorreoButton,
                verPDFButton,
                volverButton);

        // Crear la escena y asignar el layout
        Scene scene = new Scene(layout, 400, 500);

        // Agregar el archivo CSS (con el manejo de errores)
        URL cssUrl = Inventario_controller.class.getResource("src/main/java/resources/css/styles.css"); // Ruta corregida
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            ManejadorErrores.errorArchivoCSS();  // Llamada al manejador de errores si no se encuentra el CSS
        }

        stage.setScene(scene);
        stage.show();
    }

    // Método para mostrar el PDF generado del inventario
    private static void mostrarPDF() {
        String rutaInventario = "src/main/java/resources/archivos/Inventario.txt";
        String rutaPDF = "src/main/java/resources/archivos/Inventario.pdf";

        // Generar el PDF desde el archivo de inventario
        GeneradorPDF.crearPDFInventarioDesdeArchivo(rutaInventario, rutaPDF);

        try {
            File pdfFile = new File(rutaPDF);
            if (pdfFile.exists()) {
                // Abre el archivo PDF con el visor predeterminado
                java.awt.Desktop.getDesktop().open(pdfFile);
            } else {
                // Si no se encuentra el archivo PDF, muestra un mensaje de error
                ManejadorErrores.mostrarErrorGenerico("❌ No se encontró el archivo PDF generado.");
            }
        } catch (IOException ex) {
            // En caso de error al abrir el PDF, muestra el error
            ManejadorErrores.mostrarErrorGenerico("❌ No se pudo abrir el PDF.");
        }
    }


    // Método para abrir el formulario para agregar un producto
    private static void abrirFormularioAgregarProducto(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label label = new Label("Agregar Producto al Inventario");
        label.getStyleClass().add("label");

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del Producto");

        TextField cantidadField = new TextField();
        cantidadField.setPromptText("Cantidad");

        TextField unidadMedidaField = new TextField();
        unidadMedidaField.setPromptText("Unidad de Medida");

        Button agregarButton = new Button("Agregar Producto");
        agregarButton.setOnAction(e -> {
            try {
                String nombre = nombreField.getText();
                int cantidad = Integer.parseInt(cantidadField.getText());
                String unidadMedida = unidadMedidaField.getText();
                agregarProductoAlInventario(nombre, cantidad, unidadMedida);
                mostrarInventario(stage);  // Refresca el inventario
            } catch (NumberFormatException ex) {
                ManejadorErrores.errorValidacion("Cantidad");
            }
        });

        Button volverButton = new Button("Volver");
        volverButton.setOnAction(e -> mostrarInventario(stage));

        layout.getChildren().addAll(label, nombreField, cantidadField, unidadMedidaField, agregarButton, volverButton);

        Scene scene = new Scene(layout, 400, 450);
        URL resource = Inventario_controller.class.getResource("src/main/java/resources/css/styles.css"); // Ruta corregida
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            ManejadorErrores.mostrarErrorGenerico("❌ No se encontró el archivo de estilo.");
        }
        stage.setScene(scene);
        stage.show();
    }

    // Método para abrir el formulario para eliminar un producto
    private static void abrirFormularioEliminarProducto(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label label = new Label("Eliminar Producto del Inventario");
        label.getStyleClass().add("label");

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del Producto");

        Button eliminarButton = new Button("Eliminar Producto");
        eliminarButton.setOnAction(e -> {
            String nombre = nombreField.getText();
            eliminarProductoDelInventario(nombre);
            mostrarInventario(stage);  // Refresca el inventario
        });

        Button volverButton = new Button("Volver");
        volverButton.setOnAction(e -> mostrarInventario(stage));

        layout.getChildren().addAll(label, nombreField, eliminarButton, volverButton);

        Scene scene = new Scene(layout, 400, 450);
        URL resource = Inventario_controller.class.getResource("src/main/java/resources/css/styles.css"); // Ruta corregida
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            ManejadorErrores.mostrarErrorGenerico("❌ No se encontró el archivo de estilo.");
        }
        stage.setScene(scene);
        stage.show();
    }

    // Método para agregar producto
    public static void agregarProductoAlInventario(String nombre, int cantidad, String unidadMedida) {
        Producto_inventario producto = inventarioMap.get(nombre);
        if (producto != null) {
            producto.agregarCantidad(cantidad);  // Si el producto ya existe, agrega la cantidad
        } else {
            producto = new Producto_inventario(nombre, cantidad, unidadMedida);
            inventarioMap.put(nombre, producto);  // Si no existe, agrega el nuevo producto
        }
        guardarInventarioEnArchivo();  // Guarda el inventario actualizado en el archivo
    }


    // Método para eliminar producto del inventario
    public static void eliminarProductoDelInventario(String nombre) {
        Optional<Producto_inventario> productoExistente = buscarProducto(nombre);
        if (productoExistente.isPresent()) {
            // Eliminar el producto usando el nombre (clave) del Map
            inventarioMap.remove(nombre);  // Aquí usamos `nombre` como clave para remover el producto
            System.out.println("✅ Producto eliminado del inventario.");
        } else {
            System.out.println("❌ El producto no existe en el inventario.");
        }
        guardarInventarioEnArchivo();
    }


    // Método para buscar un producto
    private static Optional<Producto_inventario> buscarProducto(String nombre) {
        return inventarioMap.values().stream()  // Accede a los valores del Map
                .filter(producto -> producto.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }


    // Método para guardar inventario en archivo
    private static void guardarInventarioEnArchivo() {
        // Asegúrate de que el archivo 'Inventario.txt' exista en la ruta correcta
        File archivo = new File("src/main/java/resources/archivos/Inventario.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Iterar solo sobre los valores del Map (Producto_inventario)
            for (Producto_inventario producto : inventarioMap.values()) {
                // Construir la línea con la información del producto
                String linea = producto.getNombre() + "," + producto.getCantidad() + "," + producto.getUnidadMedida();
                writer.write(linea);
                writer.newLine();  // Agregar una nueva línea
            }
            System.out.println("✅ Inventario guardado en el archivo.");
        } catch (IOException e) {
            ManejadorErrores.errorGuardadoInventario();
        }
    }


    // Método para cargar inventario desde archivo
    public static void cargarInventarioDesdeArchivo() {
        File archivo = new File("src/main/java/resources/archivos/Inventario.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String nombre = partes[0];
                    int cantidad = Integer.parseInt(partes[1]);
                    String unidadMedida = partes[2];

                    // Crear un nuevo objeto Producto_inventario
                    Producto_inventario producto = new Producto_inventario(nombre, cantidad, unidadMedida);

                    // Agregar el producto al inventarioMap usando el nombre como clave
                    inventarioMap.put(nombre, producto);  // Aquí usamos `put` para agregar al Map
                }
            }
            System.out.println("✅ Inventario cargado desde el archivo.");
        } catch (IOException e) {
            ManejadorErrores.errorCargaInventario();
        }
    }

    // Método para volver al menú principal
    private void volverAlMenu (Stage stage){
        Menu.mostrarMenu(stage, new Usuario("Administrador", "password123", "Admin"));
    }
}
