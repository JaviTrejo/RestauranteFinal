package restaurante_gestion_de_mesas_y_comandas;

public class Mesa {

    private int numeroMesa;
    private String estado; // "libre", "ocupada", "reservada"
    private Comanda comanda; // la comanda actual de esta mesa

    public Mesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.estado = "Libre"; // Por defecto la mesa está libre
        this.comanda = null;  // No hay comanda asociada
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getEstado() {
        return estado;
    }

    // Establecer el estado de la mesa a "ocupada"
    public void ocuparMesa(Comanda comanda) {
        if (estado.equals("libre") || estado.equals("reservada")) {
            this.comanda = comanda;
            this.estado = "ocupada";
        } else {
            System.out.println("Error: La mesa ya está ocupada.");
        }
    }

    // Establecer el estado de la mesa a "reservada"
    public void reservarMesa() {
        if (estado.equals("libre")) {
            this.estado = "reservada";
        } else {
            System.out.println("Error: La mesa no está libre.");
        }
    }

    // Liberar la mesa (hacerla "libre")
    public void liberarMesa() {
        if (estado.equals("ocupada") || estado.equals("reservada")) {
            this.comanda = null;
            this.estado = "libre";
        } else {
            System.out.println("Error: La mesa ya está libre.");
        }
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }


    @Override
    public String toString() {
        return "Mesa #" + numeroMesa + " | Estado: " + estado;
    }
}
