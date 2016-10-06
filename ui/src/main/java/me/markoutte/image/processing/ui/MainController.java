package me.markoutte.image.processing.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;
import me.markoutte.segmentation.KruskalFloodFill;

import java.awt.image.BufferedImage;
import java.io.File;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().addAll(IntStream.range(0, 256).boxed().collect(Collectors.toList()));
        comboBox.setValue(0);
        comboBox.setDisable(true);
    }

    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(canvas.getScene().getWindow());
        if (file != null) {
            String uri = file.toURI().toString();
            GraphicsContext context = canvas.getGraphicsContext2D();
            image = new Image(uri);
            canvas.setWidth(image.getWidth());
            canvas.setHeight(image.getHeight());
            context.drawImage(image, 0, 0);
            comboBox.setDisable(true);
            segmentation = null;
        }
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

                openButton.setDisable(false);
                processButton.setDisable(false);
                comboBox.setDisable(false);
            });
        }
    }

    public void process() {
        if (image == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("me.markoutte.image.processing.ui.Main", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(bundle.getString("noImageChosen"));
            alert.showAndWait();
            return;
        }


        service.execute(() -> {
            processButton.setDisable(true);
            openButton.setDisable(true);


            try {
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
                comboBox.setDisable(false);
                openButton.setDisable(false);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processButton.setDisable(false);
            }
        });

    }
}
