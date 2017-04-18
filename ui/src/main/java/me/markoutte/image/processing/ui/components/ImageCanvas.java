package me.markoutte.image.processing.ui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.processing.ui.FXImageUtils;
import me.markoutte.image.processing.ui.HistogramController;

public class ImageCanvas extends Pane {

    private ObjectProperty<javafx.scene.image.Image> image;
    private final Image src;
    private final Canvas canvas;

    public ImageCanvas(Image image) {
        this.src = image;
        this.image = new SimpleObjectProperty<>();
        this.image.set(FXImageUtils.toFXImage((RectImage) image));
        this.canvas = new Canvas(getWidth(), getHeight());
        getChildren().add(canvas);
        widthProperty().addListener(e -> canvas.setWidth(getWidth()));
        heightProperty().addListener(e -> canvas.setHeight(getHeight()));

        setPrefWidth(this.image.get().getWidth());
        setPrefHeight(this.image.get().getHeight());

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                HistogramController.show("Гистограмма интересного сегмента", image, image);
            }
        });
    }

    public void setBackground(final Image background) {
        Image clone = src.clone();
        for (Pixel pixel : background) {
            if (clone.getPixel(pixel.getId()) == 0x00000000) {
                short gray = Color.getGray(background.getPixel(pixel.getId()));
                clone.setPixel(pixel.getId(), Color.combine(64, gray, gray, gray));
            }
        }
        this.image.set(FXImageUtils.toFXImage((RectImage) clone));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        javafx.scene.image.Image img = image.get();
        if (img == null) {
            return;
        }

        double ratio = img.getWidth() / img.getHeight();
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
        context.drawImage(img, x, y, width, height);
    }
}
