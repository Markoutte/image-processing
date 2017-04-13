package me.markoutte.algorithm;

import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;

import static me.markoutte.ds.Color.*;

public class Maths {

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
                opacity += getChannel(argb[i][j], Channel.OPACITY) * filter[i][j];
                red += getChannel(argb[i][j], Channel.RED) * filter[i][j];
                green += getChannel(argb[i][j], Channel.GREEN) * filter[i][j];
                blue += getChannel(argb[i][j], Channel.BLUE) * filter[i][j];
            }
        }
        return combine(normalize((int) opacity), normalize((int) red), normalize((int) green), normalize((int) blue));
    }

    public static void multiply(double[][] matrix, double multiplier) {
        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][j] = matrix[i][j] * multiplier;
            }
        }
    }

    public static int truncate(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    private Maths() {
    }
}
