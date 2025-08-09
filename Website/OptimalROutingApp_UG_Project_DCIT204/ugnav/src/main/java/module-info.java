module se16 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens se16 to javafx.fxml;
    exports se16;
}
