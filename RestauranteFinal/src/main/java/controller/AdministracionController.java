
package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import restaurante_db.Database_comida;
import restaurante_gestion_de_mesas_y_comandas.Producto;

public class AdministracionController {
    private final ObservableList<Producto> listaComida;
    private final Stage stage;  // Guardamos el Stage aquí

    // Constructor modificado para recibir el Stage
    public AdministracionController(Stage stage) {
        this.stage = stage;  // Asignamos el Stage recibido
        listaComida = FXCollections.observableArrayList(Database_comida.obtenerMenu());
    }

    public void mostrarVentanaAdministracion() {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // ListView para mostrar los productos
        ListView<Producto> listView = new ListView<>(listaComida);
        listView.setPrefHeight(200);

        // Campos de texto para el nombre y el precio
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre de la comida");

        TextField precioField = new TextField();
        precioField.setPromptText("Precio");

        // Botones para agregar, editar y eliminar productos
        Button btnAgregar = new Button("Agregar");
        Button btnEditar = new Button("Editar");
        Button btnEliminar = new Button("Eliminar");

        // Eventos para los botones
        btnAgregar.setOnAction(e -> agregarComida(nombreField.getText(), precioField.getText()));
        btnEditar.setOnAction(e -> editarComida(listView.getSelectionModel().getSelectedItem(), nombreField.getText(), precioField.getText()));
        btnEliminar.setOnAction(e -> eliminarComida(listView.getSelectionModel().getSelectedItem()));

        layout.getChildren().addAll(listView, nombreField, precioField, btnAgregar, btnEditar, btnEliminar);

        // Configuración y mostrar la ventana
        stage.setScene(new Scene(layout, 300, 400));
        stage.setTitle("Administración de Comida");
        stage.show();
    }

    private void agregarComida(String nombre, String precio) {
        try {
            double precioDouble = Double.parseDouble(precio);
            boolean agregado = Database_comida.agregarProducto(nombre, precioDouble);

            if (agregado) {
                int nuevoId = listaComida.isEmpty() ? 1 : listaComida.getLast().getId() + 1;
                Producto nuevoProducto = new Producto(nuevoId, nombre, precioDouble);
                listaComida.add(nuevoProducto);
                System.out.println("Producto agregado exitosamente al menú.");
            } else {
                System.out.println("No se pudo agregar el producto. Puede que ya exista o los datos sean inválidos.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(); // ya lo tienes
        }
    }



    private void editarComida(Producto producto, String nuevoNombre, String nuevoPrecio) {
        if (producto != null) {
            try {
                double precioDouble = Double.parseDouble(nuevoPrecio);
                producto.setNombre(nuevoNombre);
                producto.setPrecio(precioDouble);
                // Actualizamos el archivo de la base de datos
                Database_comida.guardarProductosEnArchivo();
            } catch (NumberFormatException e) {
                mostrarAlerta();
            }
        }
    }

    private void eliminarComida(Producto producto) {
        if (producto != null) {
            listaComida.remove(producto);
            Database_comida.eliminarProducto(producto.getId());
        }
    }

    private void mostrarAlerta() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText("El precio debe ser un número válido.");
        alerta.showAndWait();
    }

    // Getter para la lista de comida
    public ObservableList<Producto> obtenerListaComida() {
        return listaComida;
    }
}
