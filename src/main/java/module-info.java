module com.example.project_1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;
    requires java.desktop;
    requires org.jfree.jfreechart;
    requires JTransforms;

    opens com.example.project_1 to javafx.fxml;
    exports com.example.project_1;
}