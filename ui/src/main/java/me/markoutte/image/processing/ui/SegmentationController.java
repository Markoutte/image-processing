package me.markoutte.image.processing.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.processing.ui.components.ImageCanvas;
import me.markoutte.segmentation.Segmentation;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SegmentationController implements Initializable {

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private GridPane grid;

    @FXML
    private Pane pbwrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollpane.heightProperty().addListener(observable -> {
            int vpw = (int) scrollpane.getViewportBounds().getWidth();
            for (Node node : grid.getChildren()) {
                if (node instanceof ImageCanvas) {
                    ((ImageCanvas) node).setPrefWidth(vpw / 4);
                    ((ImageCanvas) node).setPrefHeight(vpw / 4);
                }
            }
        });
    }

    public void setImages(List<ImageCanvas.Info> images, Image background) {
        grid.getChildren().clear();
        int vpw = (int) scrollpane.getViewportBounds().getWidth();
        for (int i = 0; i < images.size(); i++) {
            ImageCanvas canvas = new ImageCanvas(images.get(i));
            canvas.setBackground(background);
            canvas.setPrefWidth(vpw / 4);
            canvas.setPrefHeight(vpw / 4);
            grid.add(canvas, i % 4, i / 4);
        }
    }

    public static Stage show(Segmentation<?> segmentation) {
        SegmentationController controller;
        Stage stage;
        try {
            FXMLLoader loader = new FXMLLoader(SegmentationController.class.getResource("segmentation.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SegmentationController.class.getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Подготовка изображений");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Application.async().submit(new Task<List<ImageCanvas.Info>>() {
            @Override
            protected List<ImageCanvas.Info> call() throws Exception {
                long start = System.currentTimeMillis();
                double[] bounds = segmentation.getHierarchy().getLevelBounds();
                RectImage image = (RectImage) segmentation.getImage();
                int low = image.width() * image.height() * 25 / 10000;
                int upper = (int) (image.width() * image.height() * 95L * 95L / 10000);

                class LevelWithSegments {
                    final int level;
                    final Map<Integer, List<Pixel>> segments;
                    public LevelWithSegments(int level, Map<Integer, List<Pixel>> segments) {
                        this.level = level;
                        this.segments = Collections.unmodifiableMap(segments);
                    }
                }

                Function<LevelWithSegments, List<ImageCanvas.Info>> myFunction = pixels -> {
                    List<ImageCanvas.Info> infos = new ArrayList<>();
                    for (Map.Entry<Integer, List<Pixel>> e : pixels.segments.entrySet()) {
                        if (e.getValue().size() > low && e.getValue().size() < upper)
                            infos.add(new ImageCanvas.Info(
                                    FXImageUtils.createImageFromPixel(e.getValue(), image.width(), image.height()),
                                    pixels.level,
                                    e.getKey(),
                                    e.getValue().size()
                            ));
                    }
                    return infos;
                };

                List<ImageCanvas.Info> result = IntStream.rangeClosed((int) bounds[0] + 1, (int) bounds[1])
                        .parallel()
                        .mapToObj(i -> new LevelWithSegments(i, segmentation.getHierarchy().getSegmentsWithValues(i)))
                        .map(myFunction)
                        .flatMap(List::stream)
                        .sorted(Comparator.comparingInt(ImageCanvas.Info::getSize))
                        .collect(Collectors.toList());
                long stop = System.currentTimeMillis();
                Journal.get().debug(String.format("Собраны %d интересных сегментов за %s мс", result.size(), (stop - start)));
                return result;
            }

            @Override
            protected void succeeded() {
                try {
                    controller.setImages(get(), segmentation.getImage());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    controller.pbwrapper.setVisible(false);
                }
            }
        });

        return stage;
    }
}
