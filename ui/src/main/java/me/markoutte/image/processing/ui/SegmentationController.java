package me.markoutte.image.processing.ui;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.processing.ui.components.ImageCanvas;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SegmentationController implements Initializable {

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private GridPane grid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollpane.heightProperty().addListener(observable -> {
            int vpw = (int) scrollpane.getViewportBounds().getWidth();
            for (Node node : grid.getChildren()) {
                if (node instanceof ImageCanvas) {
                    ((ImageCanvas) node).setPrefWidth(vpw);
                    ((ImageCanvas) node).setPrefHeight(vpw);
                }
            }
        });
    }

    public void setImages(List<Image> images) {
        grid.getChildren().clear();
        int vpw = (int) scrollpane.getViewportBounds().getWidth();
        for (int i = 0; i < images.size(); i++) {
            ImageCanvas canvas = new ImageCanvas(FXImageUtils.toFXImage((RectImage) images.get(i)));
            canvas.setPrefWidth(vpw);
            canvas.setPrefHeight(vpw);
            grid.add(canvas, 0, i);
        }
    }

    public static void show(List<Image> images, Image background) throws IOException {
        FXMLLoader loader = new FXMLLoader(SegmentationController.class.getResource("segmentation.fxml"));
        Parent root = loader.load();
        SegmentationController controller = loader.getController();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SegmentationController.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("***");

        for (Image image : images) {
            for (Pixel pixel : image) {
                if (pixel.getValue() == 0x00000000) {
                    short gray = Color.getGray(background.getPixel(pixel.getId()));
                    image.setPixel(pixel.getId(), Color.combine(64, gray, gray, gray));
                }
            }
        }

        controller.setImages(images);

        stage.show();
    }
}
