package me.markoutte.image.processing.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.markoutte.image.processing.ui.logging.JournalHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(Integer.MAX_VALUE);

        LogManager.getLogManager().readConfiguration(Application.class.getResourceAsStream("logging/logging.properties"));
        Logger.getLogger("journal").addHandler(new JournalHandler());

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

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            for (ExecutorService executor : executors) {
                executor.shutdownNow();
            }
            Platform.exit();
        });
    }

    public static void registerExecutorService(ExecutorService service) {
        executors.add(service);
    }

    public static ExecutorService async() {
        return service;
    }

    private final static List<ExecutorService> executors = new ArrayList<>();

    private static final ExecutorService service = Executors.newCachedThreadPool();
    static {
        Application.registerExecutorService(service);
    }
}
