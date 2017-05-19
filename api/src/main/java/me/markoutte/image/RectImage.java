package me.markoutte.image;

import me.markoutte.image.exceptions.NoSuchPixelException;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RectImage implements Image {

    protected int width = -1;
    protected int height = -1;

    /**
     * Создаёт пустое изображение заданных размеров
     */
    public abstract RectImage create(int width, int height);

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /**
     * Определяе формат изображения, пример:
     *
     *  path/to/image/name.png -> png
     *  path/another/image.jpeg -> jpeg
     */
    protected static String fetchFormat(String filename) {
        Pattern extensionPattern = Pattern.compile("\\.([A-Za-z0-9]+)$");
        Matcher extensionMatcher = extensionPattern.matcher(filename);
        String format = null;
        if (extensionMatcher.find()) {
            format = extensionMatcher.group(1);
        }
        return format;
    }

    public int getPixel(int x, int y) {
        checkPixelExists(x, y);
        return getPixel(y * width + x);
    }

    public void setPixel(int x, int y, int value) {
        checkPixelExists(x, y);
        setPixel(y * width + x, value);
    }

    public int[][] rect(int x1, int x2, int y1, int y2) {
        int[][] rect = new int[x2 - x1 + 1][y2 - y1 + 1];
        for (int j = Math.max(y1, 0); j < Math.min(y2 + 1, height); j++) {
            for (int i = Math.max(x1, 0); i < Math.min(x2 + 1, width); i++) {
                rect[i - x1][j - y1] = getPixel(i, j);
            }
        }
        return rect;
    }

    protected void checkPixelExists(int x, int y) throws NoSuchPixelException {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new NoSuchPixelException(String.format("Coordinates (%d, %d) out of image rect ([0, 0], [%d, %d])", x, y, width, height));
        }
    }

    @Override
    public Image clone() {
        RectImage blank = create(width, height);
        for (Pixel px : this) {
            int id = px.getId();
            blank.setPixel(id, px.getValue());
        }
        return blank;
    }

    @Override
    public Iterator<Pixel> iterator() {
        return new Iter();
    }

    private final class Iter implements Iterator<Pixel> {
        private int position = 0;

        @Override
        public boolean hasNext() {
            return 0 <= position && position < width * height;
        }

        @Override
        public Pixel next() {
            Pixel px = new Pixel(position, getPixel(position));
            position++;
            return px;
        }

        @Override
        public void remove() {
            // Не реализуется, т.к. удалять элементы из изображения в прямоугольном изображении нельзя
        }
    }

    @Override
    public int getSize() {
        return width*height;
    }

}

