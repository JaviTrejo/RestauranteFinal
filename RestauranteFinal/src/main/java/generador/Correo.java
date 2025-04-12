package generador;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Correo {

    private static final Logger logger = Logger.getLogger(Correo.class.getName());

    public static void enviarEmailConAdjunto(String tipoArchivo) {
        String[] destinatarios = {"camachohugo138@gmail.com", "yosoysalud07@gmail.com"};
        String asunto = "Reporte generado";
        String mensaje = "Hola, adjunto encontrarás el reporte generado en formato PDF.";

        // Rutas de los archivos
        String rutaTxt = "src/main/java/resources/archivos/" + tipoArchivo + ".txt";
        String rutaPDF = "src/main/java/resources/archivos/" + tipoArchivo + ".pdf";
        String titulo = "Reporte de " + tipoArchivo;  // Título dinámico dependiendo del tipo de archivo

        // 👉 Generar PDF desde el archivo de texto
        if ("inventario".equalsIgnoreCase(tipoArchivo)) {
            GeneradorPDF.crearPDFInventarioDesdeArchivo(rutaTxt, rutaPDF);
        } else if ("reporte".equalsIgnoreCase(tipoArchivo)) {
            GeneradorPDF.crearPDFReporteDesdeArchivo(rutaTxt, rutaPDF);
        }

        // Configuración SMTP
        String host = "smtp.gmail.com";
        final String usuario = "alejandrotrjsn@gmail.com"; // Reemplaza por tu correo
        final String contrasena = "jack33alex"; // Usa clave de aplicación si usas Gmail

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", host);
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, contrasena);
            }
        });

        try {
            MimeMessage mensajeCorreo = new MimeMessage(session);
            mensajeCorreo.setFrom(new InternetAddress(usuario));

            for (String destinatario : destinatarios) {
                mensajeCorreo.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            }

            mensajeCorreo.setSubject(asunto);

            MimeBodyPart cuerpo = new MimeBodyPart();
            cuerpo.setText(mensaje);

            MimeBodyPart adjunto = new MimeBodyPart();
            FileDataSource source = new FileDataSource(rutaPDF);
            adjunto.setDataHandler(new DataHandler(source));
            adjunto.setFileName(tipoArchivo + ".pdf");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpo);
            multipart.addBodyPart(adjunto);

            mensajeCorreo.setContent(multipart);

            Transport.send(mensajeCorreo);
            System.out.println("✅ Correo enviado con " + tipoArchivo + " PDF.");
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "❌ Error al enviar el correo con " + tipoArchivo + " PDF", e);
        }
    }
}
