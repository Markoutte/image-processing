package me.markoutte.algorithm;

import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;

public class Maths {

    public static double[][] gaussian(double sigma, int kernelSize) {
        double[][] result = new double[kernelSize][kernelSize];
        for (int y = 0; y < kernelSize; y++) {
            for (int x = 0; x < kernelSize; x++) {
                result[x][y] = g(x - kernelSize / 2, y - kernelSize / 2, sigma);
            }
        }
        return result;
    }

    private static double g(double x, double y, double sigma) {
        return Math.exp(-(x * x + y * y) / (2 * sigma * sigma)) / (2 * Math.PI * sigma * sigma);
    }

    public static int convolution(int[][] argb, double[][] filter) {
        if (filter.length == 0 || argb.length != filter.length || argb[0].length != filter[0].length) {
            throw new IllegalArgumentException("Matrices must have same dimension");
        }

        double opacity = 0;
        double red = 0;
        double green = 0;
        double blue = 0;
        for (int j = 0; j < filter[0].length; j++) {
            for (int i = 0; i < filter.length; i++) {
                opacity += Color.getChannel(argb[i][j], Channel.OPACITY) * filter[i][j];
                red += Color.getChannel(argb[i][j], Channel.RED) * filter[i][j];
                green += Color.getChannel(argb[i][j], Channel.GREEN) * filter[i][j];
                blue += Color.getChannel(argb[i][j], Channel.BLUE) * filter[i][j];
            }
        }
        return ((int) opacity << 24) | (((int) red) << 16) + (((int) green) << 8) | (int) blue;
    }

    private Maths() {
    }
}
