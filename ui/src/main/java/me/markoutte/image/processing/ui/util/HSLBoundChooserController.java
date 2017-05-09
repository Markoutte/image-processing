package me.markoutte.image.processing.ui.util;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.markoutte.image.HSL;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class HSLBoundChooserController implements Initializable {

    private Stage stage;

    private boolean reset = false;

    @FXML
    private Spinner<Integer> minHue;

    @FXML
    private Spinner<Integer> minSaturation;

    @FXML
    private Spinner<Integer> minIntensive;

    @FXML
    private Spinner<Integer> maxHue;

    @FXML
    private Spinner<Integer> maxSaturation;

    @FXML
    private Spinner<Integer> maxIntensive;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void setHSL(HSLBounds bounds) {
        if (bounds == null) bounds = DEFAULT;
        minHue.getValueFactory().setValue((int) (bounds.min.getHue() * 360));
        minSaturation.getValueFactory().setValue((int) (bounds.min.getSaturation() * 100));
        minIntensive.getValueFactory().setValue((int) (bounds.min.getIntensity() * 100));
        maxHue.getValueFactory().setValue((int) (bounds.max.getHue() * 360));
        maxSaturation.getValueFactory().setValue((int) (bounds.max.getSaturation() * 100));
        maxIntensive.getValueFactory().setValue((int) (bounds.max.getIntensity() * 100));
    }


    public static HSLBounds getBounds(HSLBounds prev) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("me.markoutte.image.processing.ui.Main", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(HSLBoundChooserController.class.getResource("hsl.fxml"), bundle);
            Parent root = loader.load();
            HSLBoundChooserController controller = loader.getController();
            controller.stage = new Stage();
            controller.stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(HSLBoundChooserController.class.getResource("../style.css").toExternalForm());
            controller.stage.setScene(scene);
            controller.stage.setResizable(false);
            controller.stage.setTitle("***");
            controller.setHSL(prev);
            controller.stage.showAndWait();
            if (controller.reset) {
                return DEFAULT;
            }
            return new HSLBounds(
                    new HSL(controller.minHue.getValue() / 360., controller.minSaturation.getValue() / 100., controller.minIntensive.getValue() / 100.),
                    new HSL(controller.maxHue.getValue() / 360., controller.maxSaturation.getValue() / 100., controller.maxIntensive.getValue() / 100.)
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

    public void reset() {
        reset = true;
        stage.close();
    }

    public static final HSLBounds DEFAULT = new HSLBounds(
            new HSL(0, 0, 0),
            new HSL(1, 1, 1)
    );

    public static class HSLBounds {
        public final HSL min;
        public final HSL max;

        public HSLBounds(HSL min, HSL max) {
            this.min = min;
            this.max = max;
        }
    }
}
