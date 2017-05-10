package me.markoutte.image.processing.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.markoutte.ds.Color;
import me.markoutte.image.*;
import me.markoutte.image.processing.ui.components.ImageCanvas;
import me.markoutte.image.processing.ui.util.DuplicateImageFilter;
import me.markoutte.image.processing.ui.util.BoundsPreferencesController;
import me.markoutte.segmentation.Segmentation;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class SegmentationController implements Initializable {

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private GridPane grid;

    @FXML
    private Pane pbwrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollpane.widthProperty().addListener(observable -> {
            double width = scrollpane.getViewportBounds().getWidth() / 4;
            for (Node node : grid.getChildren()) {
                if (node instanceof ImageCanvas) {
                    ((ImageCanvas) node).setPrefWidth(width);
                    ((ImageCanvas) node).setPrefHeight(width);
                }
            }
        });
    }

    public void setImages(List<ImageCanvas.Info> images, Image background) {
        pbwrapper.setVisible(false);
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

    public static Stage show(Segmentation<?> segmentation, BoundsPreferencesController.HSLBounds bounds) {
        Objects.requireNonNull(bounds);
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

        RectImage image = (RectImage) segmentation.getImage();

        Predicate<List<Pixel>> sizeCriteria = pixels -> bounds.size < pixels.size();

        Predicate<List<Pixel>> hueCriteria = pixels -> {
            List<HSL> list = pixels.stream().map(pixel -> Color.getHSL(pixel.getValue())).collect(toList());
            double h = list.stream().map(HSL::getHue).reduce(Double::sum).get() / list.size();
            double s = list.stream().map(HSL::getSaturation).reduce(Double::sum).get() / list.size();
            double l = list.stream().map(HSL::getIntensity).reduce(Double::sum).get() / list.size();
            double minHue = bounds.min.getHue();
            double maxHue = bounds.max.getHue();
            boolean hitHue;
            if (maxHue >= minHue) {
                hitHue = minHue <= h && h <= maxHue;
            } else {
                hitHue = h <= maxHue || h >= minHue;
            }
            return hitHue
                    && bounds.min.getSaturation() <= s &&  s <= bounds.max.getSaturation()
                    && bounds.min.getIntensity() <= l && l <= bounds.max.getSaturation();
        };

        final Predicate<List<Pixel>> CRITERIA = sizeCriteria.and(bounds == BoundsPreferencesController.DEFAULT ? val -> true : hueCriteria).and(Configuration.segments());

        final AtomicBoolean isCanceled = new AtomicBoolean();
        Application.async().submit(new Task<List<ImageCanvas.Info>>() {
            @Override
            protected List<ImageCanvas.Info> call() throws Exception {
                long start = System.currentTimeMillis();

                class LevelWithSegments {
                    final int level;
                    final Map<Integer, List<Pixel>> segments;

                    public LevelWithSegments(int level, Map<Integer, List<Pixel>> segments) {
                        this.level = level;
                        this.segments = Collections.unmodifiableMap(segments);
                    }
                }

                Function<LevelWithSegments, List<ImageCanvas.Info>> toInfo = pixels -> {
                    List<ImageCanvas.Info> infos = new ArrayList<>();
                    for (Map.Entry<Integer, List<Pixel>> e : pixels.segments.entrySet()) {
                        if (isCanceled.get()) {
                            Logger.getLogger("journal").severe("Work is canceled");
                            throw new CancellationException("Work is canceled");
                        }
                        if (CRITERIA.test(e.getValue()))
                            infos.add(new ImageCanvas.Info(
                                    ImageHelpers.createImageFromPixel(e.getValue(), image.width(), image.height()),
                                    pixels.level,
                                    e.getKey(),
                                    e.getValue().size()
                            ));
                    }
                    return infos;
                };

                List<ImageCanvas.Info> result = IntStream.rangeClosed(1, bounds.level)
                        .parallel()
                        .mapToObj(i -> new LevelWithSegments(i, segmentation.getSegmentsWithValues(i)))
                        .map(toInfo)
                        .flatMap(List::stream)
                        .sorted(Comparator.comparingInt(ImageCanvas.Info::getSize))
                        .collect(collectingAndThen(toList(), new DuplicateImageFilter(0.15)));

                long stop = System.currentTimeMillis();
                Logger.getLogger("journal").info(String.format("Собраны %d интересных сегментов за %s мс", result.size(), (stop - start)));
                return result;
            }

            @Override
            protected void succeeded() {
                try {
                    controller.setImages(get(), segmentation.getImage());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    ((StackPane) controller.pbwrapper.getParent()).getChildren().remove(controller.pbwrapper);
                }
            }
        });

        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> isCanceled.set(true));

        return stage;
    }
}
