module com.restaurantefinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires org.apache.pdfbox;
    requires org.slf4j;

    opens com.restaurantefinal to javafx.fxml;
    exports com.restaurantefinal;
}