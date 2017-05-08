package me.markoutte.image;

import java.io.IOException;
import java.io.InputStream;

public enum Images {
    LENNA_8("lenna_8.jpg"),
    LENNA_16("lenna_16.jpg"),
    LENNA_32("lenna_32.jpg"),
    LENNA_64("lenna_64.jpg"),
    LENNA_128("lenna_128.jpg"),
    LENNA_256("lenna_256.jpg"),
    LENNA_512("lenna_512.jpg"),;

    private final String resourceName;
    private RectImage image;

    Images(String resourceName) {
        this.resourceName = resourceName;
    }

    public final synchronized RectImage toImage() {
        if (image == null) try (InputStream stream = ImageHelpers.class.getClassLoader().getResourceAsStream("me/markoutte/image/" + resourceName)) {
            image = ImageHelpers.fromFXImage(new javafx.scene.image.Image(stream));
        } catch (IOException e) {
            throw new RuntimeException("Cannot load default image for some reason");
        }
        return image;
    }
}