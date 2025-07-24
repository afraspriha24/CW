module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.scene;
    opens com.example.demo.scene to javafx.fxml;
    exports com.example.demo.ui;
    opens com.example.demo.ui to javafx.fxml;
    exports com.example.demo.data;
    opens com.example.demo.data to javafx.fxml;
}