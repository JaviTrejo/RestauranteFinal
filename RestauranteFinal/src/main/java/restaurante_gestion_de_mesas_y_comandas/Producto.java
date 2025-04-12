package restaurante_gestion_de_mesas_y_comandas;

public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private int cantidad;

    // Constructor
    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = 1;  // Por defecto, la cantidad es 1
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Método para incrementar la cantidad del producto
    public void incrementarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }

    // Método para disminuir la cantidad del producto
    public void disminuirCantidad(int cantidad) {
        if (this.cantidad >= cantidad) {
            this.cantidad -= cantidad;
        } else {
            System.out.println("No se puede disminuir la cantidad, no hay suficiente stock.");
        }
    }

    // Método para obtener el total del producto (precio * cantidad)
    public double calcularTotal() {
        return precio * cantidad;
    }

    // Método toString para mostrar detalles del producto
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                '}';
    }
}
