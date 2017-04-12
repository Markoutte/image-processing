package me.markoutte.image.processing.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;
import me.markoutte.segmentation.KruskalFloodFill;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Button openButton;
    @FXML
    private Button processButton;
    @FXML
    private ComboBox<Integer> comboBox;

    private Image image;

    private KruskalFloodFill segmentation;

    private Stage stage;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().addAll(IntStream.range(0, 256).boxed().collect(Collectors.toList()));
        comboBox.setValue(0);
        comboBox.setDisable(true);
        processButton.setDisable(true);
        bundle = resources;

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("me/markoutte/image/processing/ui/lena-color.jpg")) {
            setImage(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* package */

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(canvas.getScene().getWindow());
        try (InputStream stream = new FileInputStream(file)) {
            setImage(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImage(InputStream stream) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        image = new Image(stream);
        canvas.setWidth(image.getWidth());
        canvas.setHeight(image.getHeight());
        context.drawImage(image, 0, 0);
        comboBox.setDisable(true);
        processButton.setDisable(false);
        segmentation = null;
        comboBox.setValue(0);
    }

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    public void changeLevel() {
        if (segmentation != null) {
            service.execute(() -> {
                openButton.setDisable(true);
                processButton.setDisable(true);
                comboBox.setDisable(true);

                Integer level = comboBox.getValue();
                Image image;

                long start = System.currentTimeMillis();
                if (level == 0) {
                    image = this.image;
                } else {
                    RectImage ri = segmentation.getImage(level);
                    WritableImage wimg = new WritableImage(ri.width(), ri.height());
                    image = SwingFXUtils.toFXImage(ri.getBufferedImage(), wimg);
                }
                GraphicsContext context = canvas.getGraphicsContext2D();
                context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                context.drawImage(image, 0, 0);

                long stop = System.currentTimeMillis();
                showPopup(String.format(bundle.getString("levelChangeTime"), level, (stop - start)));

                openButton.setDisable(false);
                processButton.setDisable(false);
                comboBox.setDisable(false);
            });
        }
    }

    public void process() {
        if (image == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(bundle.getString("noImageChosen"));
            alert.showAndWait();
            return;
        }


        service.execute(() -> {
            processButton.setDisable(true);
            openButton.setDisable(true);


            try {
                long start = System.currentTimeMillis();
                RectImage processed = new ArrayRectImage().create((int) image.getWidth(), (int) image.getHeight());
                PixelReader reader = image.getPixelReader();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        processed.setPixel(x, y, reader.getArgb(x, y));
                    }
                }

                segmentation = new KruskalFloodFill();
                segmentation.setImage(processed);
                segmentation.start();

                long stop = System.currentTimeMillis();
                showPopup(String.format(bundle.getString("processTime"), (stop - start)));

                comboBox.setDisable(false);
                openButton.setDisable(false);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processButton.setDisable(false);
            }
        });

    }

    private void showPopup(String content) {
        Platform.runLater(() -> {
            Popup popup = new Popup();
            HBox box = new HBox();
            Text text = new Text(content);
            box.getChildren().add(text);

            box.setBackground(new Background(new BackgroundFill(Color.AZURE, CornerRadii.EMPTY, Insets.EMPTY)));
            box.setPrefWidth(stage.getWidth());
            box.setPrefHeight(50);

            box.setAlignment(Pos.CENTER);

            popup.setX(stage.getX());
            popup.setY(stage.getY() + stage.getScene().getY());
            popup.getContent().add(box);

            FadeTransition ft = new FadeTransition(Duration.millis(3000), box);
            ft.setInterpolator(Interpolator.EASE_OUT);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.setOnFinished(t -> {
                popup.hide();
            });

            popup.show(stage);
            ft.play();
        });
    }

    @FXML
    public void showHistogramOfSegment(ActionEvent e) {
        System.out.println(e);
    }
}
