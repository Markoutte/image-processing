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
    private final Info src;
    private final Canvas canvas;

    public ImageCanvas(Info info) {
        this.src = info;
        this.image = new SimpleObjectProperty<>();
        this.image.set(FXImageUtils.toFXImage((RectImage) info.getImage()));
        this.canvas = new Canvas(getWidth(), getHeight());
        getChildren().add(canvas);
        widthProperty().addListener(e -> canvas.setWidth(getWidth()));
        heightProperty().addListener(e -> canvas.setHeight(getHeight()));

        setPrefWidth(this.image.get().getWidth());
        setPrefHeight(this.image.get().getHeight());

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                HistogramController.show("Гистограмма интересного сегмента", info.getImage(), info.getImage());
            }
        });
    }

    public void setBackground(final Image background) {
        Image clone = src.getImage().clone();
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

    public static final class Info {
        private final Image image;
        private final int segmentId;
        private final double level;
        private final int size;

        public Info(Image image, int segmentId, int level) {
            this(image, segmentId, level, image.getSize());
        }

        public Info(Image image, int segmentId, double level, int size) {
            this.image = image;
            this.segmentId = segmentId;
            this.level = level;
            this.size = size;
        }

        public Image getImage() {
            return image;
        }

        public int getSegmentId() {
            return segmentId;
        }

        public double getLevel() {
            return level;
        }

        public int getSize() {
            return size;
        }
    }
}
