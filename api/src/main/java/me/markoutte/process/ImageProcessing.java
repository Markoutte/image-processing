package me.markoutte.process;

import me.markoutte.algorithm.Maths;
import me.markoutte.image.Image;
import me.markoutte.image.RectImage;

import java.util.Properties;

public interface ImageProcessing {

    Image process(Image src, Properties properties);

    default RectImage filter(RectImage image, double[][] matrix, boolean ignoreAlphaChannel) {
        RectImage dest = (RectImage) image.clone();
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int[][] rect = image.rect(-matrix.length / 2 + x, matrix.length / 2 + x, -matrix[0].length / 2 + y, matrix[0].length / 2 + y);
                dest.setPixel(x, y, Maths.convolution(rect, matrix, ignoreAlphaChannel));
            }
        }

        return dest;
    }

}
