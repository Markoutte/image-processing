package me.markoutte.process;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;

import java.util.Arrays;
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
                double hue = Color.getHSL(pixel.getValue()).getHue();
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * hue)));
            }
            return out;
        }
    },

    SATURATION {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double saturation = Color.getHSL(pixel.getValue()).getSaturation();
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
                double lightness = Color.getHSL(pixel.getValue()).getIntensity();
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * lightness)));
            }
            return out;
        }
    },

    OTSU {
        @Override
        public Image process(Image src, Properties properties) {
            int threshold = Color.getOtsuThreshold(src);
            Image out = src.clone();
            for (Pixel pixel : src) {
                out.setPixel(pixel.getId(), Color.getGray(pixel.getValue()) < threshold ? 0xFF000000 : 0xFFFFFFFF);
            }
            return out;
        }
    },

    MEDIAN {
        @Override
        public Image process(Image src, Properties properties) {
            RectImage out = (RectImage) src.clone();
            Integer size = Integer.valueOf(properties.getProperty("MEDIAN.size", "3"));
            for (int y = 0; y < out.height(); y++) {
                for (int x = 0; x < out.width(); x++) {
                    int[][] rect = ((RectImage) src).rect(x - size / 2, x + size / 2, y - size / 2, y + size / 2);
                    int[] reds = new int[size * size];
                    int[] greens = new int[size * size];
                    int[] blues = new int[size * size];

                    for (int j = 0; j < size; j++) {
                        for (int i = 0; i < size; i++) {
                            int value = rect[i][j];
                            int red = Color.getChannel(value, Channel.RED);
                            int green = Color.getChannel(value, Channel.GREEN);
                            int blue = Color.getChannel(value, Channel.BLUE);
                            int index = j * size + i;

                            reds[index] = red;
                            greens[index] = green;
                            blues[index] = blue;

                            for (int k = index - 1; k >= 0 && red < reds[k]; k--) {
                                reds[k + 1] = reds[k];
                                reds[k] = red;
                            }
                            for (int k = index - 1; k >= 0 && green < greens[k]; k--) {
                                greens[k + 1] = greens[k];
                                greens[k] = green;
                            }
                            for (int k = index - 1; k >= 0 && blue < blues[k]; k--) {
                                blues[k + 1] = blues[k];
                                blues[k] = blue;
                            }
                        }
                    }

                    int median = size * size / 2 + size % 2;
                    out.setPixel(x, y, Color.combine(255, reds[median], greens[median], blues[median]));
                }
            }
            return out;
        }
    },

    EQUALIZE {
        @Override
        public Image process(Image src, Properties properties) {
            int range = 256;
            double red[] = new double[range];
            double green[] = new double[range];
            double blue[] = new double[range];
            for (Pixel pixel : src) {
                red[Color.getChannel(pixel.getValue(), Channel.RED)] += 1;
                green[Color.getChannel(pixel.getValue(), Channel.GREEN)] += 1;
                blue[Color.getChannel(pixel.getValue(), Channel.BLUE)] += 1;
            }
            for (int i = 0; i < range; i++) {
                red[i] /= src.getSize() / 255;
                green[i] /= src.getSize() / 255;
                blue[i] /= src.getSize() / 255;
            }
            for (int i = 1; i < range; i++) {
                red[i] = red[i - 1] + red[i];
                green[i] = green[i - 1] + green[i];
                blue[i] = blue[i - 1] + blue[i];
            }
            Image out = src.clone();
            for (Pixel pixel : src) {
                out.setPixel(pixel.getId(), Color.combine(
                        255,
                        (int) red[Color.getChannel(pixel.getValue(), Channel.RED)],
                        (int) green[Color.getChannel(pixel.getValue(), Channel.GREEN)],
                        (int) blue[Color.getChannel(pixel.getValue(), Channel.BLUE)]
                ));
            }
            return out;
        }
    }

    ;
}
