package me.markoutte.process.impl;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.ARGB;
import me.markoutte.image.RectImage;
import me.markoutte.process.ImageProcessing;

import java.util.Properties;
import java.util.function.IntFunction;

public enum  FilteringProcessing implements ImageProcessing {

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

            return filter((RectImage) src, filter, false);
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
            return filter((RectImage) src, matrix, false);
        }
    },

    LAPLACIAN {
        @Override
        public Image process(Image src, Properties properties) {
            double matrix[][] = {
                    {-1, -1, -1},
                    {-1, 8, -1},
                    {-1, -1, -1}
            };
            return filter((RectImage) src, matrix, true);
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
            return filter((RectImage) src, matrix, false);
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
                    out.setPixel(x, y, Color.combine(getRectRGBArrays(rect, value -> value / 2 + value % 2)));
                }
            }
            return out;
        }
    },

    MINIMA {
        @Override
        public Image process(Image src, Properties properties) {
            RectImage out = (RectImage) src.clone();
            Integer size = Integer.valueOf(properties.getProperty("MEDIAN.size", "3"));
            for (int y = 0; y < out.height(); y++) {
                for (int x = 0; x < out.width(); x++) {
                    int[][] rect = ((RectImage) src).rect(x - size / 2, x + size / 2, y - size / 2, y + size / 2);
                    out.setPixel(x, y, Color.combine(getRectRGBArrays(rect, value -> 0)));
                }
            }
            return out;
        }
    },

    MAXIMA {
        @Override
        public Image process(Image src, Properties properties) {
            RectImage out = (RectImage) src.clone();
            Integer size = Integer.valueOf(properties.getProperty("MEDIAN.size", "3"));
            for (int y = 0; y < out.height(); y++) {
                for (int x = 0; x < out.width(); x++) {
                    int[][] rect = ((RectImage) src).rect(x - size / 2, x + size / 2, y - size / 2, y + size / 2);
                    out.setPixel(x, y, Color.combine(getRectRGBArrays(rect, value -> value - 1)));
                }
            }
            return out;
        }
    },

    ;

    @Override
    public String toString() {
        return name();
    }

    protected ARGB getRectRGBArrays(int[][] rect, IntFunction<Integer> element) {
        if (rect.length == 0) {
            throw new IllegalArgumentException("Cannot use empty arrays");
        }
        int size = rect.length * rect[0].length;
        int[] reds = new int[size];
        int[] greens = new int[size];
        int[] blues = new int[size];

        for (int j = 0; j < rect[0].length; j++) {
            for (int i = 0; i < rect.length; i++) {
                int value = rect[i][j];
                int red = Color.getChannel(value, Channel.RED);
                int green = Color.getChannel(value, Channel.GREEN);
                int blue = Color.getChannel(value, Channel.BLUE);
                int index = j * rect.length + i;

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

        int el = element.apply(size);
        return new ARGB(255, reds[el], greens[el], blues[el]);
    }
}
