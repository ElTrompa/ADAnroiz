module com.example.ae4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.ae4 to javafx.fxml;
    exports com.example.ae4;
}