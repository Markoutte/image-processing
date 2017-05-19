package me.markoutte.image;

import javafx.scene.image.*;
import javafx.scene.image.Image;
import me.markoutte.ds.Color;
import me.markoutte.image.impl.ArrayRectImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ImageHelpers {
    
    public static javafx.scene.image.Image toFXImage(me.markoutte.image.RectImage image) {
        WritableImage img = new WritableImage(image.width(), image.height());
        PixelWriter pw = img.getPixelWriter();
        pw.setPixels(0, 0, image.width(), image.height(), PixelFormat.getIntArgbInstance(), image.getBytes(), 0, image.width());
        return img;
    }

    public static javafx.scene.image.Image toFXImage(me.markoutte.image.RectImage image, me.markoutte.image.RectImage background, double opacity) {
        me.markoutte.image.Image clone = image.clone();
        for (Pixel pixel : background) {
            if (clone.getPixel(pixel.getId()) == 0x00000000) {
                short gray = (short) Math.min(Color.getGray(background.getPixel(pixel.getId())) + 100, 255);
                clone.setPixel(pixel.getId(), Color.combine((int) (opacity * 255), gray, gray, gray));
            }
        }
        return ImageHelpers.toFXImage((RectImage) clone);
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

    public static RectImage fromBufferedImage(BufferedImage image) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public static BufferedImage toBufferedImage(me.markoutte.image.RectImage image) {
        int width = image.width();
        int height = image.height();
        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bf.setRGB(i, j, image.getPixel(i, j));
            }
        }
        return bf;
    }

    public static RectImage createImageFromPixel(Iterable<Pixel> pixels, int width, int height) {
        RectImage image = new ArrayRectImage().create(width, height);
        for (Pixel pixel : pixels) {
            image.setPixel(pixel.getId(), pixel.getValue());
        }
        return image;
    }

    public static boolean deepEquals(Image left, Image right) {
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

    private ImageHelpers() {
    }
}
