module com.example.project_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project_1 to javafx.fxml;
    exports com.example.project_1;
}