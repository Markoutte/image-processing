package me.markoutte.process;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;

import java.util.Properties;

public enum Algorithms implements ImageProcessing {

    GRAYSCALE {
        @Override
        public Image process(Image src, Properties properties) {
            Image clone = src.clone();
            for (Pixel pixel : src) {
                clone.setPixel(pixel.getId(), Color.getIntGray(pixel.getValue()));
            }
            return clone;
        }
    },

    BOX_BLUR {
        @Override
        public RectImage process(Image src, Properties properties) {
            Integer kernel = Integer.valueOf(properties.getProperty("BOX_BLUR.kernel", "11"));
            double[][] filter = new double[kernel][kernel];
            for (int j = 0; j < filter[0].length; j++) {
                for (int i = 0; i < filter.length; i++) {
                    filter[i][j] = 1d / kernel / kernel;
                }
            }

            return filter((RectImage) src, filter);
        }
    },

    BLUR {
        @Override
        @SuppressWarnings("unchecked")
        public  RectImage process(Image src, Properties properties) {
            Integer kernel = Integer.valueOf(properties.getProperty("BLUR.kernel", "7"));
            Double sigma = Double.valueOf(properties.getProperty("BLUR.sigma", "2"));
            double matrix[][] = Maths.gaussian(sigma, kernel);
            return filter((RectImage) src, matrix);
        }
    },

    ;

    protected RectImage filter(RectImage image, double[][] matrix) {
        RectImage dest = (RectImage) image.clone();
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                int[][] rect = image.rect(-matrix.length / 2 + x, matrix.length / 2 + x, -matrix[0].length / 2 + y, matrix[0].length / 2 + y);
                dest.setPixel(x, y, Maths.convolution(rect, matrix));
            }
        }

        return dest;
    }
}
