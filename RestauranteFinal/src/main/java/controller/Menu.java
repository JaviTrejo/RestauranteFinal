package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.restaurantefinal.restaurantefinal.Usuario;

import java.net.URL;

public class Menu {

    public static void mostrarMenu(Stage primaryStage, Usuario usuario) {
        primaryStage.setTitle("Sistema de Restaurante - Menú de " + usuario.getNombreUsuario());

        Label label = new Label("Seleccione su rol");
        label.getStyleClass().add("label");

        Button adminButton = createStyledButton("Administrador", e -> actualizarEscena(primaryStage, usuario, "Administrador"));
        Button capitanButton = createStyledButton("Capitán", e -> actualizarEscena(primaryStage, usuario, "Capitán"));
        Button meseroButton = createStyledButton("Mesero", e -> actualizarEscena(primaryStage, usuario, "Mesero"));

        VBox layout = new VBox(15, label, adminButton, capitanButton, meseroButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        // Crear la escena y asignar el layout
        Scene scene = new Scene(layout, 400, 450);

        // Agregar el archivo CSS (con el manejo de errores)
        URL cssUrl = Menu.class.getResource("/css/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            ManejadorErrores.errorArchivoCSS();  // Llamada al manejador de errores
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void actualizarEscena(Stage stage, Usuario usuario, String rol) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label label = new Label("Menú " + rol);
        label.getStyleClass().add("label");

        switch (rol) {
            case "Administrador":
                layout.getChildren().addAll(
                        createStyledButton("Gestionar Inventario", e -> abrirInventario(stage)),
                        createStyledButton("Generar Reporte", e -> abrirGenerarReporte(stage)),
                        createStyledButton("Gestionar Productos", e -> abrirAdministracionDeProductos(stage)),
                        createStyledButton("Volver", e -> mostrarMenu(stage, usuario))
                );
                break;
            case "Capitán":
                layout.getChildren().addAll(
                        createStyledButton("Hacer Descuento"),
                        createStyledButton("Gestionar Mesas", e -> abrirMesas(stage)),
                        createStyledButton("Gestionar facturación", e -> abrirFacturacion(stage)),
                        createStyledButton("Gestionar Comandas", e -> abrirComanda(stage)),
                        createStyledButton("Volver", e -> mostrarMenu(stage, usuario))
                );
                break;
            case "Mesero":
                layout.getChildren().addAll(
                        createStyledButton("Gestionar Comandas", e -> abrirComanda(stage)),
                        createStyledButton("Volver", e -> mostrarMenu(stage, usuario))
                );
                break;
        }

        // Crear la escena y asignar el layout
        Scene scene = new Scene(layout, 400, 450);

        // Agregar el archivo CSS (con el manejo de errores)
        URL cssUrl = Menu.class.getResource("/css/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            ManejadorErrores.errorArchivoCSS();  // Llamada al manejador de errores
        }

        // Establecer la escena en el escenario (stage)
        stage.setScene(scene);

    }

    private static void abrirInventario(Stage stage) {
        Inventario_controller.mostrarInventario(stage);  // Mostrar inventario al presionar el botón
    }

    private static void abrirFacturacion(Stage stage) {
        // Crear la instancia de Facturacion_controller y abrir la ventana
        // Crear el controlador de Reporte
        Facturacion_controller FacturacionController = new Facturacion_controller();
        FacturacionController.start(stage);  // Abrir la ventana de Reporte

    }

    private static void abrirMesas(Stage stage){
        Mesa_controller MesaController = new Mesa_controller();  // Pasando el Stage
        MesaController.start(stage);  // Iniciando la ventana
    }

    private static void abrirComanda(Stage stage){
        Comanda_controller comandaController = new Comanda_controller(stage);  // Pasando el Stage
        comandaController.start();  // Iniciando la ventana
    }

    private static void abrirGenerarReporte(Stage stage) {
        // Crear la instancia de Reporte_controller y abrir la ventana de reporte
        Reporte_controller reporteController = new Reporte_controller(stage);
        reporteController.generarReporte(); // Método para generar el reporte
    }

    private static void abrirAdministracionDeProductos(Stage stage) {
        // Crear una instancia de AdministracionController
        AdministracionController adminController = new AdministracionController(stage);
        adminController.mostrarVentanaAdministracion();  // Ahora puedes llamar al método de instancia
    }


    private static Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setOnAction(action);
        return button;
    }

    private static Button createStyledButton(String text) {
        return createStyledButton(text, e -> {
        });  // Acción vacía para los botones sin acción predeterminada
    }
}
