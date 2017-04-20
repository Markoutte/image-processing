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
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.processing.ui.components.ImageCanvas;
import me.markoutte.segmentation.Segmentation;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
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

        final AtomicBoolean isCanceled = new AtomicBoolean();
        Application.async().submit(new Task<List<ImageCanvas.Info>>() {
            @Override
            protected List<ImageCanvas.Info> call() throws Exception {
                long start = System.currentTimeMillis();
                double[] bounds = segmentation.getHierarchy().getLevelBounds();
                RectImage image = (RectImage) segmentation.getImage();
                int low = image.width() * image.height() * 25 / 1000;
                int upper = (int) (image.width() * image.height() * 75L / 100);

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
                        if (isCanceled.get()) {
                            throw new CancellationException("Work is canceled");
                        }
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

                List<ImageCanvas.Info> result = IntStream.range((int) bounds[0] + 1, (int) bounds[1])
                        .parallel()
                        .mapToObj(i -> new LevelWithSegments(i, segmentation.getHierarchy().getSegmentsWithValues(i)))
                        .map(myFunction)
                        .flatMap(List::stream)
                        .sorted(Comparator.comparingInt(ImageCanvas.Info::getSize))
                        .collect(Collectors.toList());
                List<ImageCanvas.Info> filteredResult = new ArrayList<>(result.size());

                double filterSize = 25. / 1000;
                for (int i = 0; i < result.size() - 1; i++) {
                    ImageCanvas.Info current = result.get(i);
                    ImageCanvas.Info next = result.get(i + 1);

                    List<ImageCanvas.Info> sameImagesButUsedBefore = new ArrayList<>();
                    for (int j = filteredResult.size() - 1; j >=0 && (next.getSize() - filteredResult.get(j).getSize()) / (double) filteredResult.get(j).getSize() < filterSize; j--) {
                        if (Objects.equals(current.getSegmentId(), filteredResult.get(j).getSegmentId())) {
                            sameImagesButUsedBefore.add(filteredResult.get(j));
                        }
                    }
                    filteredResult.removeAll(sameImagesButUsedBefore);

                    if (Objects.equals(current.getSegmentId(), next.getSegmentId())) {
                        int currentSize = current.getSize();
                        int nextSize = next.getSize();
                        if ((double) (nextSize - currentSize) / currentSize > filterSize) {
                            filteredResult.add(current);
                        }
                    } else {
                        filteredResult.add(current);
                    }
                }
                long stop = System.currentTimeMillis();
                Journal.get().debug(String.format("Собраны %d интересных сегментов за %s мс", filteredResult.size(), (stop - start)));
                return filteredResult;
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
