module w2e.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.commons.collections4;
    requires w2e.core;
    requires org.apache.commons.lang3;

    opens com.w2e.ui to javafx.fxml;
    exports com.w2e.ui;
}