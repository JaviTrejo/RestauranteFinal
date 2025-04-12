// Modelo de Usuario - representa a un usuario del sistema (Administrador, Gerente, Mesero, etc.)
package com.restaurantefinal.restaurantefinal;

public class Usuario {

    // Atributos privados (encapsulados)
    private String nombreUsuario;
    private String password;
    private String rol;

    // Constructor con parámetros
    public Usuario(String nombreUsuario, String password, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.rol = rol;
    }

    // Getters (métodos para acceder a los datos)
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    // Setters (métodos para modificar los datos)
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // Método opcional para imprimir información útil del usuario (sin mostrar la contraseña)
    @Override
    public String toString() {
        return "Usuario{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
