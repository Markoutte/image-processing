package me.markoutte.image.processing.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class HistogramController implements Initializable {

    @FXML
    private BarChart<String, Integer> reds;

    @FXML
    private BarChart<String, Integer> greens;

    @FXML
    private BarChart<String, Integer> blues;

    @FXML
    private BarChart<String, Integer> grays;

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
