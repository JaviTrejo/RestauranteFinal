package controller;

import generador.Correo;
import generador.GeneradorPDF;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import restaurante_gestion_de_mesas_y_comandas.Producto_inventario;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Reporte_controller {

    private Button btnGenerarReporte;
    private final Facturacion_controller facturacionController;
    private final Stage stage;

    // Constructor que acepta un Stage
    public Reporte_controller(Stage stage) {
        this.stage = stage;
        this.facturacionController = new Facturacion_controller();
        facturacionController.cargarVentasDesdeArchivo();
    }

    // Muestra la ventana del reporte
    public void mostrarVentanaReporte() {
        btnGenerarReporte = new Button("Generar Reporte Diario");
        btnGenerarReporte.setOnAction(e -> generarReporte());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(btnGenerarReporte);

        Scene scene = new Scene(layout, 500, 400);

        URL cssUrl = getClass().getResource("/resources/Styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("❌ El archivo CSS no se pudo cargar. Verifica la ruta: /resources/Styles.css");
        }

        stage.setTitle("Generador de Reportes");
        stage.setScene(scene);
        stage.show();
    }

    // Genera el reporte y lo abre/envía
    public void generarReporte() {
        btnGenerarReporte.setDisable(true);

        double ventasDelDia = facturacionController.obtenerVentasDelDia();
        int cantidadVentas = facturacionController.obtenerCantidadVentas();
        List<Producto_inventario> inventarioActual = obtenerInventarioFiltrado();

        boolean exito = GeneradorPDF.generarReporteDiario(ventasDelDia, cantidadVentas, inventarioActual);

        if (exito) {
            mostrarAlerta("Éxito", "El reporte se ha generado correctamente.", Alert.AlertType.INFORMATION);

            String nombrePDF = "src/main/java/resources/archivos/reportes/reporte_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

            abrirPDF(nombrePDF);
            Correo.enviarEmailConAdjunto("reporte");
        } else {
            mostrarAlerta("Error", "Hubo un problema al generar el reporte.", Alert.AlertType.ERROR);
        }

        btnGenerarReporte.setDisable(false);
    }

    // Muestra alertas gráficas
    private static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Lee el archivo de inventario y filtra por fecha
    private static List<Producto_inventario> obtenerInventarioFiltrado() {
        String rutaArchivo = "src/main/java/resources/archivos/Inventario.txt";
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Producto_inventario> inventarioFiltrado = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Evita errores si la línea está incompleta

                String dateString = parts[0];
                LocalDate fechaInventario = LocalDate.parse(dateString, formatter);

                if (fechaInventario.equals(today) || fechaInventario.equals(yesterday)) {
                    String nombre = parts[1];
                    int cantidad = Integer.parseInt(parts[2]);
                    String unidad = parts[3];
                    inventarioFiltrado.add(new Producto_inventario(nombre, cantidad, unidad));
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo de inventario: " + e.getMessage());
        }

        return inventarioFiltrado;
    }

    // Abre el archivo PDF generado
    private static void abrirPDF(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            if (archivo.exists()) {
                Desktop.getDesktop().open(archivo);
            } else {
                System.out.println("❌ No se pudo encontrar el archivo PDF.");
            }
        } catch (IOException e) {
            System.err.println("❌ Error al abrir el archivo PDF: " + e.getMessage());
        }
    }
}
