package me.markoutte.image.processing.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.markoutte.ds.Channel;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.process.ImageProcessing;
import me.markoutte.process.impl.ColorProcessing;
import me.markoutte.image.ImageHelpers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class HistogramController implements Initializable {

    @FXML
    private BarChart<String, Integer> reds;

    @FXML
    private BarChart<String, Integer> greens;

    @FXML
    private BarChart<String, Integer> blues;

    @FXML
    private BarChart<String, Integer> grays;

    @FXML
    private Canvas red;

    @FXML
    private Canvas green;

    @FXML
    private Canvas blue;

    @FXML
    private Canvas hue;

    @FXML
    private Canvas saturation;

    @FXML
    private Canvas intensity;

    @FXML
    private MenuBar menu;
    @FXML
    private MenuItem saveButton;

    private ResourceBundle bundle;

    private me.markoutte.image.Image image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac")) {
            menu.useSystemMenuBarProperty().set(true);
            saveButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
        }
    }

    public void setReds(int[] reds) {
        setData(this.reds, reds, "red");
    }

    public void setGreens(int[] greens) {
        setData(this.greens, greens, "green");
    }

    public void setBlues(int[] blues) {
        setData(this.blues, blues, "blue");
    }

    public void setGrays(int[] grays) {
        setData(this.grays, grays, "gray");
    }

    public void setImage(me.markoutte.image.Image image) {
        this.image = image;
        drawImage(red, image, ColorProcessing.RED);
        drawImage(green, image, ColorProcessing.GREEN);
        drawImage(blue, image, ColorProcessing.BLUE);
        drawImage(hue, image, ColorProcessing.HUE);
        drawImage(saturation, image, ColorProcessing.SATURATION);
        drawImage(intensity, image, ColorProcessing.INTENSITY);
    }

    private void drawImage(Canvas canvas, me.markoutte.image.Image image, ImageProcessing processing) {

        Application.async().submit(new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return ImageHelpers.toFXImage((RectImage) processing.process(image, new Properties()));
            }

            @Override
            protected void succeeded() {
                try {
                    Image drawing = get();
                    double ratio = drawing.getWidth() / drawing.getHeight();

                    double x = 0;
                    double y = 0;
                    double width = canvas.getWidth();
                    double height = canvas.getHeight();
                    if (ratio > 1) {
                        height = height / ratio;
                        y = (canvas.getHeight() - height) / 2;
                    } else {
                        width = width * ratio;
                        x = (canvas.getWidth() - width) / 2;
                    }

                    GraphicsContext context = canvas.getGraphicsContext2D();
                    context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    context.drawImage(drawing, x, y, width, height);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(BarChart<String, Integer> chart, int[] data, String color) {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), data[i]));
        }
        chart.getData().addAll(series);
        for (int i = 0; i < data.length; i++) {
            for (Node node : chart.lookupAll(".default-color" + i + ".chart-bar")) {
                node.setStyle(String.format("-fx-bar-fill: %s;", color));
            }
        }
        chart.getXAxis().setTickLabelsVisible(false);
        chart.getXAxis().setTickMarkVisible(false);
        chart.getYAxis().setTickLabelsVisible(false);
        chart.getYAxis().setTickMarkVisible(false);
    }

    public static void show(String title, Iterable<Pixel> area, me.markoutte.image.Image image) {
        int[] reds = new int[256];
        int[] greens = new int[256];
        int[] blues = new int[256];
        int[] grays = new int[256];
        int size = 0;
        for (Pixel pixel : area) {
            reds[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.RED)]++;
            blues[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.BLUE)]++;
            greens[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.GREEN)]++;
            grays[me.markoutte.ds.Color.getGray(pixel.getValue())]++;
            size++;
        }

        if (area == image) {
            Logger.getLogger("journal").info("Загружены гистограммы для изображения");
        } else {
            Logger.getLogger("journal").info(String.format("Загружены гистограммы для области размером %d", size));
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("me.markoutte.image.processing.ui.Main", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(HistogramController.class.getResource("histogram.fxml"), bundle);
            Parent root = loader.load();
            HistogramController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(HistogramController.class.getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(title);
            stage.show();

            controller.setReds(reds);
            controller.setGreens(greens);
            controller.setBlues(blues);
            controller.setGrays(grays);
            controller.setImage(image);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void saveFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(bundle.getString("menu.file.save"));
        File file = chooser.showSaveDialog(red.getScene().getWindow());
        if (file != null) {
            String path = file.getAbsolutePath();
            if (!path.endsWith(".png")) {
                path += ".png";
            }
            BufferedImage bimg = ImageHelpers.toBufferedImage((RectImage) image);
            try {
                ImageIO.write(bimg, "png", new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
