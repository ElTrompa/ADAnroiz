module org.example.ae4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.ae4 to javafx.fxml;
    exports org.example.ae4;
}