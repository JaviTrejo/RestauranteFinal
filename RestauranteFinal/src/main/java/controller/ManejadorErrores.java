/*
ManejadorErrores.errorArchivoCSS();  // Cuando no se encuentra el archivo CSS
ManejadorErrores.errorValidacion("Cantidad");  // Si hay un error en la validación de la cantidad
ManejadorErrores.errorCargaInventario();  // Si hay un problema al cargar el inventario
ManejadorErrores.errorGuardadoInventario();  // Si hay un problema al guardar el inventario
ManejadorErrores.mostrarErrorGenerico("Algo salió mal.");  // Error genérico
*/

package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class ManejadorErrores {

    // Método para mostrar error genérico
    public static void mostrarErrorGenerico(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Mostrar advertencia
    public static void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Error específico para campos vacíos o inválidos
    public static void errorValidacion(String campo) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Campo inválido");
        alerta.setHeaderText("Validación requerida");
        alerta.setContentText("⚠️ El campo '" + campo + "' no puede estar vacío o es incorrecto.");
        alerta.showAndWait();
    }

    // Error al intentar convertir algo, como el precio
    public static void errorConversionNumero(String campo) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error de formato");
        alerta.setHeaderText("Dato inválido");
        alerta.setContentText("❌ El campo '" + campo + "' debe contener un número válido.");
        alerta.showAndWait();
    }

    public static void mostrarConfirmacionImpresion() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Impresión Exitosa");
        alert.setHeaderText(null);
        alert.setContentText("✅ La comanda ha sido impresa correctamente.");
        alert.showAndWait();
    }


    // Método para mostrar un error relacionado con el archivo CSS
    public static void errorArchivoCSS() {
        mostrarMensaje("Error al cargar el CSS", "No se pudo encontrar el archivo CSS.");
    }


    // Método para mostrar un error al cargar el inventario
    public static void errorCargaInventario() {
        mostrarMensaje("Error al cargar el inventario", "Hubo un problema al cargar el inventario desde el archivo.");
    }

    // Método para mostrar un error al guardar el inventario
    public static void errorGuardadoInventario() {
        mostrarMensaje("Error al guardar el inventario", "Hubo un problema al guardar el inventario en el archivo.");
    }

    // Método para mostrar una alerta con el tipo de mensaje que se quiera (informativo, de advertencia, etc.)
    public static void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);  // Opcional: Puedes poner un texto encabezado si lo deseas
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para mostrar un mensaje de error
    public static void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarConfirmacionPago(String metodo, double total) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pago procesado");
        alert.setHeaderText(null);
        alert.setContentText("✅ Pago procesado exitosamente.\nMétodo de pago: " + metodo + "\nTotal pagado: $" + total);
        alert.showAndWait();
    }
}


