package me.markoutte.image.processing.ui.util;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import me.markoutte.image.HSL;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class BoundsPreferencesController implements Initializable {

    private Stage stage;

    private Object result = UNKNOWN;

    public static final Integer UNKNOWN = 0x00;
    public static final Integer SAVE = 0x01;
    public static final Integer RESET = 0x02;

    @FXML
    private Slider minHue;

    @FXML
    private Slider minSaturation;

    @FXML
    private Slider minIntensive;

    @FXML
    private Slider maxHue;

    @FXML
    private Slider maxSaturation;

    @FXML
    private Slider maxIntensive;

    @FXML
    private Spinner<Integer> minSize;

    @FXML
    private Spinner<Integer> maxLevel;

    private HSLBounds bounds = DEFAULT;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InvalidationListener mins = observable -> update(minHue, minSaturation, minIntensive);
        minHue.valueProperty().addListener(mins);
        minIntensive.valueProperty().addListener(mins);

        InvalidationListener maxs = observable -> update(maxHue, maxSaturation, maxIntensive);
        maxHue.valueProperty().addListener(maxs);
        maxIntensive.valueProperty().addListener(maxs);
    }

    private void update(Slider hue, Slider saturation, Slider intensity) {
        Node track = saturation.lookup(".track");
        Color from = Color.hsb(0, 0, intensity.getValue() / 100);
        Color to = Color.hsb(hue.getValue(), 1, 1);
        track.setStyle("-fx-background-color: " + gradient(from, to));
    }

    private static String gradient(Color from, Color to) {
        return String.format("linear-gradient(to right, %s, %s)", web(from), web(to));
    }

    private static String web(Color color) {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255),
                (int)( color.getGreen() * 255),
                (int)( color.getBlue() * 255 ));
    }

    private void setHSL(HSLBounds bounds) {
        this.bounds = bounds == null ? DEFAULT : bounds;

        minHue.setValue((int) (this.bounds.min.getHue() * 360));
        minSaturation.setValue((int) (this.bounds.min.getSaturation() * 100));
        minIntensive.setValue((int) (this.bounds.min.getIntensity() * 100));
        maxHue.setValue((int) (this.bounds.max.getHue() * 360));
        maxSaturation.setValue((int) (this.bounds.max.getSaturation() * 100));
        maxIntensive.setValue((int) (this.bounds.max.getIntensity() * 100));
        minSize.getValueFactory().setValue(this.bounds.size);

        update(minHue, minSaturation, minIntensive);
        update(maxHue, maxSaturation, maxIntensive);
    }


    public static HSLBounds getBounds(HSLBounds prev) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("me.markoutte.image.processing.ui.Main", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(BoundsPreferencesController.class.getResource("hsl.fxml"), bundle);
            Parent root = loader.load();
            BoundsPreferencesController controller = loader.getController();
            controller.stage = new Stage();
            controller.stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BoundsPreferencesController.class.getResource("../style.css").toExternalForm());
            controller.stage.setScene(scene);
            controller.stage.setResizable(false);
            controller.stage.setTitle("Настройка условий выборки");
            controller.stage.initStyle(StageStyle.UTILITY);
            controller.stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> controller.setHSL(prev));
            controller.stage.showAndWait();
            if (controller.result == RESET) {
                return DEFAULT;
            }
            return new HSLBounds(
                    new HSL(controller.minHue.getValue() / 360., controller.minSaturation.getValue() / 100., controller.minIntensive.getValue() / 100.),
                    new HSL(controller.maxHue.getValue() / 360., controller.maxSaturation.getValue() / 100., controller.maxIntensive.getValue() / 100.),
                    controller.minSize.getValue(),
                    controller.maxLevel.getValue()
            );

        } catch (Exception err) {
            err.printStackTrace();
        }
        return DEFAULT;
    }

    private static HSL hsl(Color color) {
        if (color == Color.RED) {
            return new HSL(1, 1, 1);
        }
        return me.markoutte.ds.Color.getHSL(color.hashCode());
    }

    public void save() {
        result = SAVE;
        stage.close();
    }

    public void reset() {
        result = RESET;
        stage.close();
    }

    public static final HSLBounds DEFAULT = new HSLBounds(
            new HSL(0, 0, 0),
            new HSL(1, 1, 1),
            100, 10
    );

    public static class HSLBounds {
        public final HSL min;
        public final HSL max;
        public final int size;
        public final int level;

        public HSLBounds(HSL min, HSL max, int minSize, int maxLevel) {
            this.min = min;
            this.max = max;
            this.size = minSize;
            this.level = maxLevel;
        }
    }
}
