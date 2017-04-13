package me.markoutte.image.processing.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("me.markoutte.image.processing.ui.Main", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("main.fxml"), bundle);
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle(bundle.getString("application.name"));
        primaryStage.show();
    }
}
