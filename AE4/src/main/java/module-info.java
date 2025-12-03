module com.example.ae4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;

    opens com.example.ae4 to javafx.fxml, org.hibernate.orm.core;
    exports com.example.ae4;
}
