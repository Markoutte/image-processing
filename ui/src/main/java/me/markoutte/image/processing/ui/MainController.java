package me.markoutte.image.processing.ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;
import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Hierarchy;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;
import me.markoutte.process.ImageProcessing;
import me.markoutte.process.impl.ColorProcessing;
import me.markoutte.process.impl.FilteringProcessing;
import me.markoutte.process.impl.HistogramProcessing;
import me.markoutte.segmentation.Segmentation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainController implements Initializable {

    @FXML
    private MenuBar menu;
    @FXML
    private Menu menuFilters;
    @FXML
    private Canvas canvas;
    @FXML
    private ScrollPane imagesp;
    @FXML
    private MenuItem openButton;
    @FXML
    private MenuItem saveButton;
    @FXML
    private MenuButton processButton;
    @FXML
    private ComboBox<Integer> comboBox;
    @FXML
    private MenuItem prevImage;
    @FXML
    private MenuItem nextImage;

    private Stage journal;

    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private Image drawn = null;

    private final List<Image> history = new ArrayList<>();

    private final ObjectProperty<Segmentation<RectImage>> segmentation = new SimpleObjectProperty<>();

    private final BooleanProperty uiLock = new SimpleBooleanProperty();

    private Stage stage;

    private ResourceBundle bundle;

    private Properties properties;

    private double scale = 1;

    private final Journal jou = Journal.get();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().addAll(IntStream.range(0, 256).boxed().collect(Collectors.toList()));
        comboBox.setValue(0);
        comboBox.setDisable(true);
        processButton.setDisable(true);
        bundle = resources;

        patchMacOS();

        image.addListener((observable, oldValue, newValue) -> {
            processButton.setDisable(newValue == null);
            segmentation.setValue(null);

            drawImage(newValue);

            addHistory: if (newValue != null && !history.contains(newValue)) {
                // Просто добавили новое значение в конец истории
                if (history.size() == 0 || history.get(history.size() - 1) == oldValue) {
                    history.add(newValue);
                    jou.debug(String.format("Изображение добавлено в историю. Всего в истории %d изображений", history.size()));
                    break addHistory;
                }

                // Когда старый элемент где-то внутри истории, при этом новое значение ни из текущего списка
                int i = history.indexOf(oldValue);
                if (i >= 0) {
                    history.subList(i + 1, history.size()).clear();
                    history.add(newValue);
                    jou.debug(String.format("Изображение добавлено в историю начиная с позиции %d. Всего в истории %d изображений", i + 1, history.size()));
                }
            }

            prevImage.setDisable(history.size() <= 1 || history.get(0) == newValue);
            nextImage.setDisable(history.size() <= 1 || history.get(history.size() - 1) == newValue);
            comboBox.setDisable(segmentation.getValue() == null);
            comboBox.setValue(0);

            if (newValue == null) {
                jou.error("Удалено изображение");
            }
        });

        uiLock.addListener((observable, oldValue, newValue) -> {
            menuFilters.setDisable(newValue || image.getValue() == null);
            processButton.setDisable(newValue || image.getValue() == null);
            prevImage.setDisable(newValue || history.size() <= 1 || history.get(0) == image.get());
            nextImage.setDisable(newValue || history.size() <= 1 || history.get(history.size() - 1) == image.get());
            comboBox.setDisable(newValue || segmentation.get() == null);
            openButton.setDisable(newValue);
        });

        properties = new Properties();
        try (InputStream stream = getClass().getResourceAsStream("algorithms.properties")) {
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<MenuItem> colorProcessingItems = new ArrayList<>();
        Menu colorProcessing = new Menu(bundle.getString("menu.filters.color"));
        for (ColorProcessing algorithm : ColorProcessing.values()) {
            MenuItem item = new MenuItem(bundle.containsKey(algorithm.name()) ? bundle.getString(algorithm.name()) : algorithm.name());
            colorProcessingItems.add(item);
            item.setOnAction(event -> preprocess(algorithm));
        }
        colorProcessing.getItems().addAll(colorProcessingItems);
        menuFilters.getItems().add(colorProcessing);

        List<MenuItem> filteringProcessingItems = new ArrayList<>();
        Menu filteringProcessing = new Menu(bundle.getString("menu.filters.filtering"));
        for (FilteringProcessing algorithm : FilteringProcessing.values()) {
            MenuItem item = new MenuItem(bundle.containsKey(algorithm.name()) ? bundle.getString(algorithm.name()) : algorithm.name());
            filteringProcessingItems.add(item);
            item.setOnAction(event -> preprocess(algorithm));
        }
        filteringProcessing.getItems().addAll(filteringProcessingItems);
        menuFilters.getItems().add(filteringProcessing);

        List<MenuItem> histogramProcessingItems = new ArrayList<>();
        Menu histogramProcessing = new Menu(bundle.getString("menu.filters.histogram"));
        for (HistogramProcessing algorithm : HistogramProcessing.values()) {
            MenuItem item = new MenuItem(bundle.containsKey(algorithm.name()) ? bundle.getString(algorithm.name()) : algorithm.name());
            histogramProcessingItems.add(item);
            item.setOnAction(event -> preprocess(algorithm));
        }
        histogramProcessing.getItems().addAll(histogramProcessingItems);
        menuFilters.getItems().add(histogramProcessing);

        List<MenuItem> heuristics = new ArrayList<>();
        for (ColorHeuristics heuristic : ColorHeuristics.values()) {
            MenuItem item = new MenuItem(bundle.containsKey(heuristic.name()) ? bundle.getString(heuristic.name()) : heuristic.name());
            item.setOnAction(event -> process(heuristic));
            heuristics.add(item);
        }
        processButton.getItems().addAll(heuristics);
    }

    /* package */
    void setStage(Stage stage) {
        this.stage = stage;
        this.stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream("me/markoutte/image/processing/ui/lena-color.jpg")) {
                image.set(new Image(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.imagesp.widthProperty().addListener(observable -> drawImage(image.get()));
        this.imagesp.heightProperty().addListener(observable -> drawImage(image.get()));
    }

    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(bundle.getString("openButton.text"));
        File file = chooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }
        try (InputStream stream = new FileInputStream(file)) {
            Image image = new Image(stream);
            jou.info(String.format("Загружено новое изображение (%dpx×%dpx) из %s", (int) image.getWidth(), (int) image.getHeight(), file.getName()));
            this.image.set(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(bundle.getString("menu.file.save"));
        File file = chooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            String path = file.getAbsolutePath();
            if (!path.endsWith(".png")) {
                path += ".png";
            }
            RectImage image;
            if (segmentation.get() != null) {
                image = segmentation.get().getImage(comboBox.getValue());
            } else {
                image = FXImageUtils.fromFXImage(this.image.get());
            }
            BufferedImage bimg = FXImageUtils.toBufferedImage(image);
            try {
                ImageIO.write(bimg, "png", new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    public void changeLevel() {
        uiLock.setValue(true);
        service.execute(new Task<Long>() {

            @Override
            protected Long call() throws Exception {
                Integer level = comboBox.getValue();
                Image image;

                long start = System.currentTimeMillis();
                if (level == 0) {
                    image = MainController.this.image.get();
                } else {
                    image = FXImageUtils.toFXImage(segmentation.get().getImage(level));
                }
                drawImage(image);

                long stop = System.currentTimeMillis();
                return stop - start;
            }

            @Override
            protected void succeeded() {
                try {
                    jou.debug(String.format(bundle.getString("levelChangeTime"), comboBox.getValue(), get()));
//                    showPopup(String.format(bundle.getString("levelChangeTime"), comboBox.getValue(), get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    uiLock.setValue(false);
                }
            }
        });
    }

    private void drawImage(Image image) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (image != null) {
            double width = image.getWidth() * scale;
            double height = image.getHeight() * scale;

            int x = Math.max((int) (imagesp.getViewportBounds().getWidth() - width) / 2, 0);
            int y = Math.max((int) (imagesp.getViewportBounds().getHeight() - height) / 2, 0);

            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.setLayoutX(x);
            canvas.setLayoutY(y);
            context.drawImage(image, 0, 0, width, height);
        } else {
            canvas.setWidth(0);
            canvas.setHeight(0);
        }

        drawn = image;
    }

    public void preprocess(ImageProcessing processor) {
        RectImage oldValue = FXImageUtils.fromFXImage(this.image.get());
        long start = System.currentTimeMillis();
        me.markoutte.image.RectImage newValue = (RectImage) processor.process(oldValue, properties);        long stop = System.currentTimeMillis();
        jou.info(String.format(bundle.getString("preprocessTime"), (stop - start), processor));
        if (!Objects.equals(newValue, oldValue)) {
            this.image.set(FXImageUtils.toFXImage(newValue));
        }
    }

    public void process(Heuristics heuristics) {
        if (image.get() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(bundle.getString("noImageChosen"));
            alert.showAndWait();
            return;
        }

        uiLock.setValue(true);

        Task<Long> task = new Task<Long>() {

            @Override
            protected Long call() throws Exception {
                long start = System.currentTimeMillis();
                RectImage processed = FXImageUtils.fromFXImage(image.get());

                Segmentation<RectImage> ff = Configuration.segmentation.newInstance();
                ff.setImage(processed);
                ff.setHeuristic(heuristics);
                ff.start();

                long stop = System.currentTimeMillis();

                segmentation.set(ff);
                return stop - start;
            }

            @Override
            protected void succeeded() {
                try {
                    jou.info(String.format(bundle.getString("processTime"), get(), heuristics));
//                    showPopup(String.format(bundle.getString("processTime"), get(), heuristics));
                    comboBox.setValue(0);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    uiLock.setValue(false);
                }
            }
        };

        service.submit(task);

    }

    @FXML
    public void textControlPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL && segmentation.get() != null) {
            canvas.setCursor(Cursor.CROSSHAIR);
        }
    }

    @FXML
    public void textControlReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    public void showHistogramOfSegment(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (segmentation.getValue() == null) {
                RectImage image = FXImageUtils.fromFXImage(this.image.get());
                showHistograms(bundle.getString("fullImageHist"), image, image);
                return;
            }

            double x = e.getX() / scale;
            double y = e.getY() / scale;
            Hierarchy hierarchy = segmentation.get().getHierarchy();
            Integer level = comboBox.getValue();
            if (level == 0 || hierarchy == null) {
                RectImage image = segmentation.get().getImage(0);
                showHistograms(bundle.getString("fullImageHist"), image, image);
                return;
            }
            RectImage image = (RectImage) hierarchy.getSourceImage();
            int segment = hierarchy.getSegment( (int) y * image.width() + (int) x, level);
            List<Pixel> area = hierarchy.getArea(segment, level);
            if (e.isControlDown()) {
                String title = String.format(bundle.getString("partlyImageHist"), segment % image.width(), segment / image.height(), level, area.size());
                showHistograms(title, area, createImageFromPixel(area, image.width(), image.height()));
            } else {
                showHistograms(bundle.getString("fullImageHist"), image, image);
            }
        }
    }

    private void showHistograms(String title, Iterable<Pixel> area, me.markoutte.image.Image image) {
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
            jou.info(String.format(bundle.getString("loadImageHistogram"), ((RectImage) image).width(), ((RectImage) image).height()));
        } else {
            jou.info(String.format(bundle.getString("loadAreaHistogram"), size));
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("histogram.fxml"));
            Parent root = loader.load();
            HistogramController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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

    private RectImage createImageFromPixel(Iterable<Pixel> pixels, int width, int height) {
        RectImage image = new ArrayRectImage().create(width, height);
        for (Pixel pixel : pixels) {
            image.setPixel(pixel.getId(), pixel.getValue());
        }
        return image;
    }

    @FXML
    public void setPrevImage() {
        int i = history.indexOf(image.get());
        Image image = history.get(i - 1);
        jou.debug(String.format("Выбрано изображение из истории с позицией %d (%d×%d)", i, (int) image.getWidth(), (int) image.getHeight()));
        this.image.set(image);
    }

    @FXML
    public void setNextImage() {
        int i = history.indexOf(image.get());
        Image image = history.get(i + 1);
        jou.debug(String.format("Выбрано изображение из истории с позицией %d (%d×%d)", i + 2, (int) image.getWidth(), (int) image.getHeight()));
        this.image.set(image);
    }

    @FXML
    public void changeZoom(ScrollEvent event) {
        if (!event.isControlDown()) {
            return;
        }
        if (event.getDeltaY() < 0) {
            scale = Math.max(scale * 0.8, 1./16);
        } else {
            scale = Math.min(scale * 1.25, 4);
        }

        drawImage(drawn);
        event.consume();
    }

    private void patchMacOS() {
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac")) {
            menu.useSystemMenuBarProperty().set(true);
            openButton.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
            saveButton.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
            prevImage.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN));
            nextImage.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
        }
    }

    public void openJournal() {
        try {
            if (journal == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("journal.fxml"));
                Parent root = loader.load();
                JournalController controller = loader.getController();
                journal = new Stage();
                controller.setStage(journal);
                journal.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                journal.setScene(scene);
                journal.setTitle(bundle.getString("journal.title"));
            }
            journal.show();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
