package me.markoutte.image.processing.ui.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;

public final class Converters {

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

    private Converters() {
    }
}
