package me.markoutte.process.impl;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.process.ImageProcessing;

import java.util.Properties;

public enum  FilteringProcessing implements ImageProcessing {

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

}
