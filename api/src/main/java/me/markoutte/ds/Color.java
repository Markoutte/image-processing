package me.markoutte.ds;

import me.markoutte.algorithm.Maths;

import static java.lang.Math.*;
import static java.lang.Math.min;

public final class Color {

    public static int getChannel(int pixel, Channel channel) {
        switch (channel) {
            case OPACITY:
                return pixel >> 24 & 0xFF;
            case RED:
                return pixel >> 16 & 0xFF;
            case GREEN:
                return pixel >> 8 & 0xFF;
            case BLUE:
                return pixel & 0xFF;
        }
        throw new IllegalStateException();
    }

    public static short getGray(int pixel) {
        return (short) Math.round(0.2126 * getChannel(pixel, Channel.RED) + 0.7152 * getChannel(pixel, Channel.GREEN) + 0.0722 * getChannel(pixel, Channel.BLUE));
    }

    public static int combine(int alpha, int red, int green, int blue) {
        return (alpha << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | blue & 0xFF;
    }

    public static int getIntGray(int brightess) {
        return combine(0xFF, brightess, brightess, brightess);
    }

    public static int getBlank(int pixel) {
        return 0;
    }

    public static int normalize(int pixel) {
        return Maths.truncate(pixel, 0, 255);
    }

    public static double getHue(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;

        double h = acos(.5 * ((r - g) + (r - b)) / pow(pow(r - g, 2) + (r - b) * (g - b), 0.5));
        if (b > g) {
            h = 2 * PI - h;
        }
        return h;
    }

    public static double getSaturation(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;
        return  1 - 3 * min(min(r, g), b) / (r + g + b);
    }

    public static double getLightness(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;
        return (r + g + b) / 3.;
    }

    private Color() {
    }
}
