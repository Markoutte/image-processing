package me.markoutte.image.impl;

import me.markoutte.image.Image;
import me.markoutte.image.RectImage;

import java.util.Arrays;

public final class ArrayRectImage extends RectImage {

    int[] image = null;

    public void setBytes(int[] argbArray) {
        this.image = argbArray;
    }

    public int[] getBytes() {
        return image;
    }

    @Override
    public RectImage create(int width, int height) {
        ArrayRectImage image = new ArrayRectImage();
        image.width = width;
        image.height = height;
        image.image = new int[width*height];
        return image;
    }

    @Override
    public int getPixel(int id) {
        checkPixelExists(id % width, id / width);
        return image[id];
    }

    @Override
    public void setPixel(int id, int value) {
        checkPixelExists(id % width, id / width);
        image[id] = value;
    }

    @Override
    public void setPixel(int x, int y, int value) {
        checkPixelExists(x, y);
        image[width * y + x] = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayRectImage pixels = (ArrayRectImage) o;

        if (width != pixels.width) return false;
        if (height != pixels.height) return false;
        return Arrays.equals(image, pixels.image);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

}

