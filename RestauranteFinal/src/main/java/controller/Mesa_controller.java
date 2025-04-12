package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import restaurante_db.Database_mesas;
import restaurante_gestion_de_mesas_y_comandas.Comanda;
import restaurante_gestion_de_mesas_y_comandas.Mesa;
import restaurante_gestion_de_mesas_y_comandas.Producto;

import java.util.ArrayList;
import java.util.List;

public class Mesa_controller {

    private final ObservableList<Mesa> mesas = FXCollections.observableArrayList();
    private final TableView<Mesa> tablaMesas = new TableView<>();
    private int comandaIdCounter = 1; // Contador de ID para las comandas

    public void start (Stage primaryStage) {
        primaryStage.setTitle("Gestión de Mesas");

        // Cargar mesas desde el archivo
        List<Mesa> listaMesas = Database_mesas.getMesas();
        mesas.setAll(listaMesas);  // Agregar las mesas a la tabla

        // Tabla de mesas
        TableColumn<Mesa, Integer> colNumero = new TableColumn<>("Mesa #");
        colNumero.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getNumeroMesa()).asObject());

        TableColumn<Mesa, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));

        tablaMesas.getColumns().add(colNumero);
        tablaMesas.getColumns().add(colEstado);
        tablaMesas.setItems(mesas);

        // Botones
        Button btnReservar = new Button("Reservar Mesa");
        Button btnOcupar = new Button("Ocupar Mesa");
        Button btnLiberar = new Button("Liberar Mesa");
        Button btnAgregar = new Button("Agregar Mesa");
        Button btnEliminar = new Button("Eliminar Mesa");

        btnReservar.setOnAction(e -> reservarMesa());
        btnOcupar.setOnAction(e -> ocuparMesa());
        btnLiberar.setOnAction(e -> liberarMesa());
        btnAgregar.setOnAction(e -> agregarMesa());
        btnEliminar.setOnAction(e -> eliminarMesa());

        // Diseño
        HBox botones = new HBox(10, btnReservar, btnOcupar, btnLiberar, btnAgregar, btnEliminar);
        botones.setPadding(new Insets(10));

        VBox layout = new VBox(10, tablaMesas, botones);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void reservarMesa() {
        Mesa seleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (seleccionada != null && seleccionada.getEstado().equals("libre")) {
            seleccionada.reservarMesa();
            Database_mesas.actualizarMesa(seleccionada);
            tablaMesas.refresh();
            ManejadorErrores.mostrarMensaje("Éxito", "Mesa reservada con éxito.");
        } else {
            ManejadorErrores.mostrarError("Error", "La mesa no está libre para ser reservada.");
        }
    }

    private void ocuparMesa() {
        Mesa seleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (seleccionada != null && seleccionada.getEstado().equals("reservada")) {
            List<Producto> productosVacios = new ArrayList<>();
            Comanda nuevaComanda = new Comanda(comandaIdCounter++, seleccionada, productosVacios);
            seleccionada.ocuparMesa(nuevaComanda);
            Database_mesas.actualizarMesa(seleccionada);
            tablaMesas.refresh();
            ManejadorErrores.mostrarMensaje("Éxito", "Mesa ocupada con éxito.");
        } else {
            ManejadorErrores.mostrarError("Error", "La mesa no está reservada o ya está ocupada.");
        }
    }

    private void liberarMesa() {
        Mesa seleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (seleccionada != null && !seleccionada.getEstado().equals("libre")) {
            seleccionada.liberarMesa();
            Database_mesas.actualizarMesa(seleccionada);
            tablaMesas.refresh();
            ManejadorErrores.mostrarMensaje("Éxito", "Mesa liberada con éxito.");
        } else {
            ManejadorErrores.mostrarError("Error", "La mesa ya está libre.");
        }
    }

    private void agregarMesa() {
        int nuevoNumero = obtenerSiguienteNumeroMesa();
        Mesa nuevaMesa = new Mesa(nuevoNumero);
        mesas.add(nuevaMesa);
        Database_mesas.agregarMesa(nuevaMesa);
        tablaMesas.refresh();
        ManejadorErrores.mostrarMensaje("Mesa agregada", "Se agregó la mesa #" + nuevoNumero + " correctamente.");
    }

    private int obtenerSiguienteNumeroMesa() {
        return mesas.stream()
                .mapToInt(Mesa::getNumeroMesa)
                .max()
                .orElse(0) + 1;
    }


    private void eliminarMesa() {
        Mesa seleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mesas.remove(seleccionada);
            Database_mesas.eliminarMesa(seleccionada.getNumeroMesa());
            tablaMesas.refresh();
            ManejadorErrores.mostrarMensaje("Mesa eliminada", "Se eliminó la mesa correctamente.");
        } else {
            ManejadorErrores.mostrarError("Error", "Selecciona una mesa para eliminar.");
        }
    }
}
