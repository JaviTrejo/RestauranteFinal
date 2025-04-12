/* Constructores para el mandejo de la porcion de suministros que seran consumidas desde el inventario */

package restaurante_gestion_de_mesas_y_comandas;

public class Producto_inventario {

    private final String nombre;
    private int cantidad;
    private final String unidadMedida; // Ej: kg, lts, piezas

    public Producto_inventario(String nombre, int cantidad, String unidadMedida) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    // Agregar cantidad al inventario
    public void agregarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }

    // Descontar cantidad al vender o preparar
    public void descontarCantidad(int cantidad) {
        this.cantidad -= cantidad;
        if (this.cantidad < 0) {
            this.cantidad = 0;
        }
    }

    @Override
    public String toString() {
        return nombre + " | " + cantidad + " " + unidadMedida;
    }
}
