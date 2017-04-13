package me.markoutte.process;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;

import java.util.Properties;

import static java.lang.Math.*;

// https://en.wikipedia.org/wiki/Kernel_(image_processing)
public enum Algorithms implements ImageProcessing {

    GRAYSCALE {
        @Override
        public Image process(Image src, Properties properties) {
            Image clone = src.clone();
            for (Pixel pixel : src) {
                clone.setPixel(pixel.getId(), Color.getIntGray(Color.getGray(pixel.getValue())));
            }
            return clone;
        }
    },

    BOX {
        @Override
        public RectImage process(Image src, Properties properties) {
            Integer kernel = Integer.valueOf(properties.getProperty("BOX.kernel", "3"));
            double[][] filter = new double[kernel][kernel];
            for (int j = 0; j < filter[0].length; j++) {
                for (int i = 0; i < filter.length; i++) {
                    filter[i][j] = 1d / kernel / kernel;
                }
            }

            return filter((RectImage) src, filter);
        }
    },

    GAUSS {
        @Override
        public Image process(Image src, Properties properties) {
            double matrix[][] = {
                    {1, 2, 1},
                    {2, 4, 2},
                    {1, 2, 1}
            };
            Maths.multiply(matrix, 1./16);
            return filter((RectImage) src, matrix);
        }
    },

    SHARPEN {
        @Override
        @SuppressWarnings("unchecked")
        public  RectImage process(Image src, Properties properties) {
            double matrix[][] = {
                    {0, -1, 0},
                    {-1, 5, -1},
                    {0, -1, 0}
            };
            return filter((RectImage) src, matrix);
        }
    },

    RED {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, Color.getChannel(pixel.getValue(), Channel.RED), 0, 0));
            }
            return out;
        }
    },

    GREEN {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, 0, Color.getChannel(pixel.getValue(), Channel.GREEN), 0));
            }
            return out;
        }
    },

    BLUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, 0, 0, Color.getChannel(pixel.getValue(), Channel.BLUE)));
            }
            return out;
        }
    },

    HUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : src) {
                double hue = Color.getHue(pixel.getValue());
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * hue / (2 * PI))));
            }
            return out;
        }
    },

    SATURATION {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double saturation = Color.getSaturation(pixel.getValue());
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * saturation)));
            }
            return out;
        }
    },

    INTENSITY {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double lightness = Color.getIntensity(pixel.getValue());
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * lightness)));
            }
            return out;
        }
    }
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
