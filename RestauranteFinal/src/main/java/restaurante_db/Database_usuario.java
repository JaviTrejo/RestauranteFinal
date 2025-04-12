package restaurante_db;

import com.restaurantefinal.restaurantefinal.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database_usuario {

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final List<Usuario> usuarios = new ArrayList<>();

    // Cargar usuarios desde el archivo al iniciar la aplicación
    static {
        cargarUsuarios();
    }

    // Devuelve toda la lista de usuarios
    public static List<Usuario> obtenerUsuarios() {
        return usuarios;
    }

    // Busca un usuario por el nombre de usuario
    public static Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null; // No encontró al usuario
    }

    // Agregar un usuario y guardar en el archivo
    public static void agregarUsuario(String nombreUsuario, String password, String rol) {
        usuarios.add(new Usuario(nombreUsuario, password, rol));
        guardarUsuarios();
    }

    // Eliminar un usuario y actualizar el archivo
    public static boolean eliminarUsuario(String nombreUsuario) {
        Usuario usuario = buscarUsuario(nombreUsuario);
        if (usuario != null) {
            usuarios.remove(usuario);
            guardarUsuarios();
            System.out.println("🗑️ Usuario eliminado exitosamente.");
            return true;
        } else {
            System.out.println("⚠️ Usuario no encontrado.");
            return false;
        }
    }

    // Editar un usuario y actualizar el archivo
    public static boolean editarUsuario(String nombreUsuario, String nuevaPassword, String nuevoRol) {
        Usuario usuario = buscarUsuario(nombreUsuario);
        if (usuario != null) {
            usuarios.remove(usuario);
            usuarios.add(new Usuario(nombreUsuario, nuevaPassword, nuevoRol));
            guardarUsuarios();
            System.out.println("✏️ Usuario editado exitosamente.");
            return true;
        } else {
            System.out.println("⚠️ Usuario no encontrado.");
            return false;
        }
    }

    // Guardar la lista de usuarios en un archivo
    private static void guardarUsuarios() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario u : usuarios) {
                writer.write(u.getNombreUsuario() + "," + u.getPassword() + "," + u.getRol());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error al guardar usuarios: " + e.getMessage());
        }
    }

    // Cargar la lista de usuarios desde el archivo
    private static void cargarUsuarios() {
        usuarios.clear(); // Limpiar la lista antes de cargar
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            // Si el archivo no existe, crear usuarios por defecto
            agregarUsuario("admin", "admin123", "Administrador");
            agregarUsuario("gerente", "gerente123", "Gerente");
            agregarUsuario("capitan", "capitan123", "Capitan");
            agregarUsuario("mesero", "mesero123", "Mesero");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    usuarios.add(new Usuario(partes[0], partes[1], partes[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error al cargar usuarios: " + e.getMessage());
        }
    }
}
