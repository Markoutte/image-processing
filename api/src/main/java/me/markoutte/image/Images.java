package me.markoutte.image;

import java.io.IOException;
import java.io.InputStream;

public enum Images {
    GROUND("ground.jpg"),
    LENA("lena-color.jpg"),
    CARTER("carter.jpg"),
    COOKIES("cookies.jpg"),
    SUMMER("summer.jpg"),
    MEAL("meal.jpg"),
    EARCH("earch.jpg");

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