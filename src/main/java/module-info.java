module com.example.hotelmanagement {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.persistence;

    opens com.example.hotelmanagement to javafx.fxml, org.hibernate.orm.core;

    exports com.example.hotelmanagement;
    exports com.example.hotelmanagement.Views;
    opens com.example.hotelmanagement.Views to javafx.fxml;
    opens com.example.hotelmanagement.Models to org.hibernate.orm.core;
    opens com.example.hotelmanagement.DTO to javafx.base;
    opens com.example.hotelmanagement.Utils to org.hibernate.orm.core; // Example if HibernateUtils is in Utils package


    //ADD
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires MaterialFX;
    requires org.kordamp.ikonli.fontawesome;
    requires fontawesomefx;
    requires javafx.swing;
    requires io;
    requires kernel;
    requires layout;
    requires org.apache.poi.ooxml;
    requires VirtualizedFX;

}
