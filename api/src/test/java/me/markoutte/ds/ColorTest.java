package me.markoutte.ds;

import javafx.scene.image.PixelReader;
import me.markoutte.image.HSL;
import me.markoutte.image.Image;
import me.markoutte.image.impl.ArrayRectImage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class ColorTest {

    private final String resource = "me/markoutte/image/test-pic.png";

    private Image png;

    public enum C {
        BLACK, RED, MAGENTA, GREEN, GRAY, YELLOW, CYAN, BLUE, WHITE;
    }

    @Before
    public void init() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
//            BufferedImage image = ImageIO.read(stream);
//            png = new ArrayRectImage().create(image.getWidth(), image.getHeight());
//            for (int y = 0; y < image.getHeight(); y++) {
//                for (int x = 0; x < image.getWidth(); x++) {
//                    png.setPixel(y * image.getWidth() + x, image.getRGB(x, y));
//                }
//            }

            javafx.scene.image.Image image = new javafx.scene.image.Image(stream);
            png = new ArrayRectImage().create((int) image.getWidth(), (int) image.getHeight());
            PixelReader reader = image.getPixelReader();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    png.setPixel((int) (y * image.getWidth() + x), reader.getArgb(x, y));
                }
            }
        }
    }

    @Test
    public void getRGB() {
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.RED.ordinal()), Channel.RED));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.RED.ordinal()), Channel.GREEN));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.RED.ordinal()), Channel.BLUE));

        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.GREEN.ordinal()), Channel.RED));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.GREEN.ordinal()), Channel.GREEN));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.GREEN.ordinal()), Channel.BLUE));

        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.BLUE.ordinal()), Channel.RED));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.BLUE.ordinal()), Channel.GREEN));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.BLUE.ordinal()), Channel.BLUE));
    }

    @Test
    public void getCMY() {
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.MAGENTA.ordinal()), Channel.RED));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.MAGENTA.ordinal()), Channel.GREEN));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.MAGENTA.ordinal()), Channel.BLUE));

        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.CYAN.ordinal()), Channel.RED));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.CYAN.ordinal()), Channel.GREEN));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.CYAN.ordinal()), Channel.BLUE));

        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.YELLOW.ordinal()), Channel.RED));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.YELLOW.ordinal()), Channel.GREEN));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.YELLOW.ordinal()), Channel.BLUE));
    }

    @Test
    public void getBGW() {
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.BLACK.ordinal()), Channel.RED));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.BLACK.ordinal()), Channel.GREEN));
        Assert.assertEquals(0, Color.getChannel(png.getPixel(C.BLACK.ordinal()), Channel.BLUE));

        Assert.assertEquals(128, Color.getChannel(png.getPixel(C.GRAY.ordinal()), Channel.RED));
        Assert.assertEquals(128, Color.getChannel(png.getPixel(C.GRAY.ordinal()), Channel.GREEN));
        Assert.assertEquals(128, Color.getChannel(png.getPixel(C.GRAY.ordinal()), Channel.BLUE));

        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.WHITE.ordinal()), Channel.RED));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.WHITE.ordinal()), Channel.GREEN));
        Assert.assertEquals(255, Color.getChannel(png.getPixel(C.WHITE.ordinal()), Channel.BLUE));
    }

    @Test
    public void getGray() {
        Assert.assertEquals(0, Color.getGray(png.getPixel(C.BLACK.ordinal())));
        Assert.assertEquals(128, Color.getGray(png.getPixel(C.GRAY.ordinal())));
        Assert.assertEquals(255, Color.getGray(png.getPixel(C.WHITE.ordinal())));
    }

    @Test
    public void hsl2rgb() {
        int rgb = Color.combine(255, 64, 128, 192);
        HSL hsl = Color.getHSL(rgb);
        Assert.assertEquals(64, Color.getRed(Color.getRGB(hsl)));
        Assert.assertEquals(128, Color.getGray(Color.getRGB(hsl)));
        Assert.assertEquals(192, Color.getBlue(Color.getRGB(hsl)));
    }

}