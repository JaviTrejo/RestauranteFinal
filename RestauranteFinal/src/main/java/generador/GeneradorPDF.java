package generador;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restaurante_gestion_de_mesas_y_comandas.Producto_inventario;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GeneradorPDF {

    private static final Logger logger = LoggerFactory.getLogger(GeneradorPDF.class);

    public static void crearPDFInventarioDesdeArchivo(String rutaArchivoTxt, String rutaPDFSalida) {
        generarPDFDesdeArchivo(rutaArchivoTxt, rutaPDFSalida, "Reporte de Inventario");
    }

    public static void crearPDFReporteDesdeArchivo(String rutaArchivoTxt, String rutaPDFSalida) {
        generarPDFDesdeArchivo(rutaArchivoTxt, rutaPDFSalida, "Reporte Diario");
    }


    public static boolean generarReporteDiario(double ventasDelDia, int cantidadVentas, List<Producto_inventario> inventarioActual) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fecha = today.format(formatter);
        String nombreArchivo = "src/main/java/resources/archivos/reportes/reporte_" + fecha + ".pdf";

        // Crear el directorio si no existe
        File directorio = new File("src/main/java/resources/archivos/reportes");
        if (!directorio.exists()) {
            if (!directorio.mkdirs()) {
                System.out.println("❌ Error al crear la carpeta de reportes.");
                return false;
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(nombreArchivo)) {
            PdfWriter writer = new PdfWriter(fileOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("====== REPORTE DIARIO ======"));
            document.add(new Paragraph("Fecha: " + fecha));
            document.add(new Paragraph("Ventas totales del día: $" + String.format("%.2f", ventasDelDia)));
            document.add(new Paragraph("Cantidad de ventas realizadas: " + cantidadVentas));
            document.add(new Paragraph("---- INVENTARIO ACTUAL ----"));

            if (inventarioActual.isEmpty()) {
                document.add(new Paragraph("Inventario vacío."));
            } else {
                for (Producto_inventario producto : inventarioActual) {
                    document.add(new Paragraph("- " + producto.getNombre() + ": " + producto.getCantidad() + " " + producto.getUnidadMedida()));
                }
            }

            document.add(new Paragraph("\n===== Fin del reporte ====="));
            document.close();

            System.out.println("✅ Reporte guardado en: " + nombreArchivo);
            return true;

        } catch (IOException | com.itextpdf.kernel.PdfException e) {
            System.err.println("❌ Error al generar el reporte PDF: " + e.getMessage());
            return false;
        }
    }


    private static void generarPDFDesdeArchivo(String rutaArchivoTxt, String rutaPDFSalida, String titulo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivoTxt));
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Título centrado
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(220, 750);
            contentStream.showText(titulo);
            contentStream.endText();

            float margin = 50;
            float yStart = 700;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20;
            float cellMargin = 5;

            contentStream.setFont(PDType1Font.HELVETICA, 12);

            String line;
            List<String[]> filas = new ArrayList<>();
            int columnas = 0;

            while ((line = reader.readLine()) != null) {
                String[] valores = line.split("\\|");
                columnas = Math.max(columnas, valores.length);
                filas.add(valores);
            }

            float colWidth = tableWidth / columnas;

            for (String[] fila : filas) {
                float x = margin;
                yPosition -= rowHeight;

                for (int i = 0; i < columnas; i++) {
                    String texto = i < fila.length ? fila[i] : "";

                    contentStream.beginText();
                    contentStream.newLineAtOffset(x + cellMargin, yPosition + 5);
                    contentStream.showText(texto);
                    contentStream.endText();

                    x += colWidth;
                }

                if (yPosition <= margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = yStart;
                }
            }

            contentStream.close();
            document.save(rutaPDFSalida);
            logger.info("📄 PDF generado correctamente en: " + rutaPDFSalida);

        } catch (IOException e) {
            logger.error("❌ Error al generar el PDF desde el archivo: " + rutaArchivoTxt, e);
        }
    }
}
