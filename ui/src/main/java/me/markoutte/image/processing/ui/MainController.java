package me.markoutte.image.processing.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Hierarchy;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.process.Algorithms;
import me.markoutte.process.ImageProcessing;
import me.markoutte.segmentation.Segmentation;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Button preprocessButton;
    @FXML
    private ComboBox<Integer> comboBox;
    @FXML
    private ComboBox<ImageProcessing> processing;
    @FXML
    private Button prevImage;
    @FXML
    private Button nextImage;

    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    private final List<Image> history = new ArrayList<>();

    private final ObjectProperty<Segmentation<RectImage>> segmentation = new SimpleObjectProperty<>();

    private final BooleanProperty uiLock = new SimpleBooleanProperty();

    private Stage stage;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().addAll(IntStream.range(0, 256).boxed().collect(Collectors.toList()));
        comboBox.setValue(0);
        comboBox.setDisable(true);
        processing.getItems().addAll(Algorithms.values());
        processing.setValue(Algorithms.values()[0]);
        processButton.setDisable(true);
        bundle = resources;

        image.addListener((observable, oldValue, newValue) -> {
            processButton.setDisable(newValue == null);
            processing.setDisable(newValue == null);
            preprocessButton.setDisable(newValue == null);
            segmentation.setValue(null);

            GraphicsContext context = canvas.getGraphicsContext2D();
            context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            if (newValue != null) {
                canvas.setWidth(newValue.getWidth());
                canvas.setHeight(newValue.getHeight());
                context.drawImage(newValue, 0, 0, newValue.getWidth(), newValue.getHeight());
            } else {
                canvas.setWidth(0);
                canvas.setHeight(0);
            }

            if (newValue != null && !history.contains(newValue)) {
                // Просто добавили новое значение в конец истории
                if (history.size() == 0 || history.get(history.size() - 1) == oldValue) {
                    history.add(newValue);
                }

                // Когда старый элемент где-то внутри истории, при этом новое значение ни из текущего списка
                int i = history.indexOf(oldValue);
                if (i >= 0) {
                    history.subList(i + 1, history.size()).clear();
                    history.add(newValue);
                }
            }

            prevImage.setDisable(history.size() <= 1 || history.get(0) == newValue);
            nextImage.setDisable(history.size() <= 1 || history.get(history.size() - 1) == newValue);
        });

        segmentation.addListener((observable, oldValue, newValue) -> {
            comboBox.setDisable(newValue == null);
            comboBox.setValue(0);
        });

        uiLock.addListener((observable, oldValue, newValue) -> {
            processing.setDisable(newValue || image.getValue() == null);
            processButton.setDisable(newValue || image.getValue() == null);
            preprocessButton.setDisable(newValue || image.getValue() == null);
            prevImage.setDisable(newValue && history.size() <= 1 || history.get(0) == image.get());
            nextImage.setDisable(newValue && history.size() <= 1 || history.get(history.size() - 1) == image.get());
            comboBox.setDisable(newValue || segmentation.get() == null);
            openButton.setDisable(newValue);
        });

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("me/markoutte/image/processing/ui/lena-color.jpg")) {
            image.set(new Image(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* package */
    void setStage(Stage stage) {
        this.stage = stage;
    }

    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(canvas.getScene().getWindow());
        try (InputStream stream = new FileInputStream(file)) {
            image.set(new Image(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    public void changeLevel() {
        service.execute(() -> {
            uiLock.setValue(true);

            try {
                Integer level = comboBox.getValue();
                Image image;

                long start = System.currentTimeMillis();
                if (level == 0) {
                    image = this.image.get();
                } else {
                    image = FXImageUtils.toFXImage(segmentation.get().getImage(level));
                }
                drawImage(image);

                long stop = System.currentTimeMillis();
                showPopup(String.format(bundle.getString("levelChangeTime"), level, (stop - start)));
            } finally {
                uiLock.setValue(false);
            }
        });
    }

    private void drawImage(Image image) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.drawImage(image, 0, 0);
    }

    public void preprocess() {
        ImageProcessing value = processing.getValue();
        RectImage oldValue = FXImageUtils.fromFXImage(this.image.get());
        me.markoutte.image.RectImage newValue = value.process(oldValue);
        if (!Objects.equals(newValue, oldValue)) {
            this.image.set(FXImageUtils.toFXImage(newValue));
        }
    }

    public void process() {
        if (image.get() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(bundle.getString("noImageChosen"));
            alert.showAndWait();
            return;
        }

        service.execute(() -> {
            uiLock.setValue(true);

            try {
                long start = System.currentTimeMillis();
                RectImage processed = FXImageUtils.fromFXImage(image.get());

                Segmentation<RectImage> ff = Configuration.segmentation.newInstance();
                ff.setImage(processed);
                ff.start();

                long stop = System.currentTimeMillis();
                showPopup(String.format(bundle.getString("processTime"), (stop - start)));

                segmentation.set(ff);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                uiLock.setValue(false);
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
    public void showHistogramOfSegment(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (segmentation.getValue() == null) {
                System.out.println("No segmentation found");
                return;
            }

            double x = e.getX();
            double y = e.getY();
            Hierarchy hierarchy = segmentation.get().getHierarchy();
            if (comboBox.getValue() == 0 || hierarchy == null) {
                showHistograms(bundle.getString("fullImageHist"), segmentation.get().getImage(0));
                return;
            }
            RectImage image = (RectImage) hierarchy.getSourceImage();
            int segment = hierarchy.getSegment((int) (y * image.width() + x), comboBox.getValue());
            List<Pixel> area = hierarchy.getArea(segment, comboBox.getValue());
            showHistograms(String.format(bundle.getString("partlyImageHist"), segment / image.width(), segment % image.height(), comboBox.getValue(), area.size()), area);
        }
    }

    private void showHistograms(String title, Iterable<Pixel> area) {
        int[] reds = new int[256];
        int[] greens = new int[256];
        int[] blues = new int[256];
        int[] grays = new int[256];
        for (Pixel pixel : area) {
            reds[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.RED)]++;
            blues[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.BLUE)]++;
            greens[me.markoutte.ds.Color.getChannel(pixel.getValue(), Channel.GREEN)]++;
            grays[me.markoutte.ds.Color.getGray(pixel.getValue())]++;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("histogram.fxml"));
            Parent root = loader.load();
            HistogramController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            controller.setReds(reds);
            controller.setGreens(greens);
            controller.setBlues(blues);
            controller.setGrays(grays);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @FXML
    public void setPrevImage() {
        int i = history.indexOf(image.get());
        image.set(history.get(i - 1));
    }

    @FXML
    public void setNextImage() {
        int i = history.indexOf(image.get());
        image.set(history.get(i + 1));
    }
}
