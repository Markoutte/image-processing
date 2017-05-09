package me.markoutte.image.processing.ui.components;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.ImageHelpers;
import me.markoutte.image.processing.ui.HistogramController;

import java.util.Objects;

public class ImageCanvas extends StackPane {

    private javafx.scene.image.Image image;
    private final Info src;
    private final Canvas canvas;
    private final VBox box;
    private boolean trim;

    public ImageCanvas(Info info) {
        this.src = info;
        this.image = ImageHelpers.toFXImage((RectImage) info.getImage());
        this.canvas = new Canvas(getWidth(), getHeight());

//        setStyle("-fx-background-color: #FFFFFF;");

        box = new VBox();
        String defaultCss = "-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 10px;";
        String backgroundCss = defaultCss + "-fx-background-color: rgba(255, 255, 255, .9);";
        box.setStyle(defaultCss);
        box.setVisible(false);
        box.getChildren().add(new Label(String.valueOf(String.format("LV: %d", (int) info.level))));
        box.getChildren().add(new Label(String.valueOf(String.format("ID: %d", info.segmentId))));
        box.getChildren().add(new Label(String.valueOf(String.format("SZ: %d", info.size))));

        this.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> box.setVisible(true));
        this.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            boolean isSelected = Objects.equals(box.getStyle(), backgroundCss);
            box.setVisible(isSelected);
            if (!isSelected) box.setStyle(defaultCss);
        });
        box.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                HistogramController.show("Гистограмма интересного сегмента", info.getImage(), info.getImage());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                box.setStyle(Objects.equals(box.getStyle(), defaultCss) ? backgroundCss : defaultCss);
            } else if (event.getButton() == MouseButton.PRIMARY) {
                trim = !trim;
                requestLayout();
            }
        });
        box.setCursor(Cursor.HAND);

        getChildren().addAll(canvas, box);
    }

    public Info getInfo() {
        return src;
    }

    public void setBackground(final Image background) {
        Image clone = src.getImage().clone();
        for (Pixel pixel : background) {
            if (clone.getPixel(pixel.getId()) == 0x00000000) {
                short gray = (short) Math.min(Color.getGray(background.getPixel(pixel.getId())) + 100, 255);
                clone.setPixel(pixel.getId(), Color.combine(192, gray, gray, gray));
            }
        }
        this.image = ImageHelpers.toFXImage((RectImage) clone);
    }

    @Override
    protected void layoutChildren() {
        javafx.scene.image.Image img = image;
        if (img == null) {
            return;
        }
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());

        double x = 0;
        double y = 0;
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (trim) {
            int sx = Integer.MAX_VALUE;
            int sy = Integer.MAX_VALUE;
            int sw = 0;
            int sh = 0;
            RectImage image = (RectImage) src.getImage();
            for (Pixel pixel : image) {
                if (Color.getChannel(pixel.getValue(), Channel.OPACITY) != 0) {
                    sx = Math.min(pixel.getId() % image.width(), sx);
                    sy = Math.min(pixel.getId() / image.width(), sy);
                    sw = Math.max(pixel.getId() % image.width(), sw);
                    sh = Math.max(pixel.getId() / image.width(), sh);
                }
            }
            sw = sw - sx;
            sh = sh - sy;
            double ratio = sw * 1. / sh;
            if (ratio > 1) {
                height = height / ratio;
                y = (canvas.getHeight() - height) / 2;
            } else {
                width = width * ratio;
                x = (canvas.getWidth() - width) / 2;
            }
            context.drawImage(img, sx, sy, sw, sh, x, y, width, height);
        } else {
            double ratio = img.getWidth() / img.getHeight();
            if (ratio > 1) {
                height = height / ratio;
                y = (canvas.getHeight() - height) / 2;
            } else {
                width = width * ratio;
                x = (canvas.getWidth() - width) / 2;
            }
            context.drawImage(img, x, y, width, height);
        }

        super.layoutChildren();
    }

    public void trim(boolean trim) {
        this.trim = trim;
    }

    public static final class Info {
        private final Image image;
        private final int segmentId;
        private final double level;
        private final int size;

        public Info(Image image, int segmentId, int level) {
            this(image, segmentId, level, image.getSize());
        }

        public Info(Image image, double level, int segmentId, int size) {
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
