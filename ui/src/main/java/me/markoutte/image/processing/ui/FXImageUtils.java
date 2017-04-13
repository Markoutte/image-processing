package me.markoutte.image.processing.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;

public final class FXImageUtils {

    public static javafx.scene.image.Image toFXImage(me.markoutte.image.RectImage image) {
        WritableImage img = new WritableImage(image.width(), image.height());
        PixelWriter pw = img.getPixelWriter();
        pw.setPixels(0, 0, image.width(), image.height(), PixelFormat.getIntArgbInstance(), image.getBytes(), 0, image.width());
        return img;
    }

    public static me.markoutte.image.RectImage fromFXImage(javafx.scene.image.Image image) {
        RectImage processed = new ArrayRectImage().create((int) image.getWidth(), (int) image.getHeight());
        PixelReader reader = image.getPixelReader();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                processed.setPixel(x, y, reader.getArgb(x, y));
            }
        }
        return processed;
    }

    public static boolean equals(Image left, Image right) {
        if (left == right && left != null) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }

        if (left.getWidth() != right.getWidth()) {
            return false;
        }

        if (left.getHeight() != right.getHeight()) {
            return false;
        }

        PixelReader leftPixelReader = left.getPixelReader();
        PixelReader rightPixelReader = right.getPixelReader();
        for (int y = 0; y < left.getHeight(); y++) {
            for (int x = 0; x < left.getWidth(); x++) {
                if (leftPixelReader.getArgb(x, y) != rightPixelReader.getArgb(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    private FXImageUtils() {
    }
}
