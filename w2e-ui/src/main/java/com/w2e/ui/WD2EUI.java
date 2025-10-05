package com.w2e.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WD2EUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WD2EUI.class.getResource("wd2e.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(WD2EUI.class.getResource("src/main/resources/com.w2e/wd2e.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Convert word document into excel.");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}