package com.restaurantefinal.restaurantefinal;

import controller.Inventario_controller;
import controller.Menu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.restaurantefinal.restaurantefinal.Usuario;
import restaurante_db.Database_usuario;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Cargar inventario desde archivo al iniciar
        Inventario_controller.cargarInventarioDesdeArchivo();

        // Crear los elementos de la interfaz de usuario
        Label labelUsuario = new Label("Usuario:");
        TextField campoUsuario = new TextField();
        Label labelPassword = new Label("Contraseña:");
        PasswordField campoPassword = new PasswordField();
        Button botonLogin = new Button("Iniciar Sesión");
        Label mensajeError = new Label();

        // Layout (disposición de los elementos en la ventana)
        VBox layout = new VBox(10);
        layout.getChildren().addAll(labelUsuario, campoUsuario, labelPassword, campoPassword, botonLogin, mensajeError);

        // Acción del botón de login
        botonLogin.setOnAction(e -> {
            String nombreUsuario = campoUsuario.getText();
            String password = campoPassword.getText();

            // Autenticar usuario
            Usuario u = autenticarUsuario(nombreUsuario, password);

            if (u != null) {
                // Si la autenticación es correcta, abrir el menú
                abrirMenu(primaryStage, u);
            } else {
                // Si la autenticación falla, mostrar mensaje de error
                mensajeError.setText("❌ Usuario o contraseña incorrectos.");
            }
        });

        // Configuración de la escena y la ventana
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Sistema de Restaurante - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para autenticar usuario
    public static Usuario autenticarUsuario(String nombreUsuario, String password) {
        Usuario usuario = Database_usuario.buscarUsuario(nombreUsuario);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }

    // Método para abrir el menú después de iniciar sesión
    private void abrirMenu(Stage primaryStage, Usuario usuario) {
        Menu.mostrarMenu(primaryStage, usuario); // Pasa el objeto Usuario a Menu
    }

    // Método principal para iniciar la aplicación JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}
