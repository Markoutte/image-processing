package me.markoutte.image.processing.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import me.markoutte.image.RectImage;
import me.markoutte.process.Algorithms;
import me.markoutte.process.ImageProcessing;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
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

    private final ExecutorService service = Executors.newFixedThreadPool(10);

    public void setImage(me.markoutte.image.Image image) {
        drawImage(red, image, Algorithms.RED);
        drawImage(green, image, Algorithms.GREEN);
        drawImage(blue, image, Algorithms.BLUE);
        drawImage(hue, image, Algorithms.HUE);
        drawImage(saturation, image, Algorithms.SATURATION);
        drawImage(intensity, image, Algorithms.INTENSITY);
    }

    private void drawImage(Canvas canvas, me.markoutte.image.Image image, ImageProcessing processing) {

        service.submit(new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return FXImageUtils.toFXImage((RectImage) processing.process(image, new Properties()));
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
}
