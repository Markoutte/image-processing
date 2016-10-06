package me.markoutte.image.processing.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.stage.FileChooser;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;
import me.markoutte.segmentation.KruskalFloodFill;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private TextField urlField;
    @FXML
    private MenuBar menu;
    @FXML
    private Button processButton;
    @FXML
    private Slider levelSlider;
    @FXML
    private ProgressIndicator progress;

    private Image image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith("Mac")) {
            menu.useSystemMenuBarProperty().set(true);
        }
        levelSlider.setVisible(false);
    }

    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(menu.getScene().getWindow());
        if (file != null) {
            String uri = file.toURI().toString();
            GraphicsContext context = canvas.getGraphicsContext2D();
            image = new Image(uri);
            canvas.setWidth(image.getWidth());
            canvas.setHeight(image.getHeight());
            context.drawImage(image, 0, 0);
        }
    }

    private final ExecutorService service = Executors.newSingleThreadExecutor();

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

            try {
                RectImage processed = new ArrayRectImage().create((int) image.getWidth(), (int) image.getHeight());
                PixelReader reader = image.getPixelReader();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        processed.setPixel(x, y, reader.getArgb(x, y));
                    }
                }

                progress.setProgress(0.1);

                KruskalFloodFill segmentation = new KruskalFloodFill();
                segmentation.setImage(processed);
                segmentation.start();

                progress.setProgress(0.2);

                int[] ints = {5};
                double v = 0.8d / ints.length;
                for (int i : ints) {
                    long startTime = System.currentTimeMillis();
                    ArrayRectImage img = (ArrayRectImage) segmentation.getImage(i);
                    img.save(String.format("/Users/markoutte/Developer/Image/lena-%d.bmp".intern(), i));
                    progress.setProgress(progress.getProgress() + v);
                    System.out.println(String.format("Total time for level %d is %dms", i, (System.currentTimeMillis() - startTime)));
                }

                progress.setProgress(1);
                levelSlider.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processButton.setDisable(false);
            }
        });

    }
}
