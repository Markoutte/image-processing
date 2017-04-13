package me.markoutte.image.impl;

import me.markoutte.image.Image;
import me.markoutte.image.RectImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public BufferedImage getBufferedImage() {
        BufferedImage bf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bf.setRGB(i, j, image[width*j + i]);
            }
        }
        return bf;
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

}

