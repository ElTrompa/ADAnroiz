module org.example.fichajes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires com.google.gson;

    opens org.example.fichajes to javafx.fxml;
    opens org.example.fichajes.model to org.hibernate.orm.core;
    exports org.example.fichajes;
    exports org.example.fichajes.model;
    exports org.example.fichajes.dao;
    exports org.example.fichajes.util;
}