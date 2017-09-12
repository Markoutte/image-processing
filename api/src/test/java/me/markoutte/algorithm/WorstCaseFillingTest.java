package me.markoutte.algorithm;

import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayRectImage;
import me.markoutte.segmentation.NaiveFloodFill;

public class WorstCaseFillingTest {

    public static void main(String[] args) {
        int width = 15;
        int height = 7;
        RectImage pixels = new ArrayRectImage().create(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels.setPixel(i, j, 1);
            }
        }

        NaiveFloodFill.testCenter(pixels, 1);
    }

}
