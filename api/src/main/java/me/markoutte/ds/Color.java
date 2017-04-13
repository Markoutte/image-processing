package me.markoutte.ds;

import me.markoutte.algorithm.Maths;

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

    public static int getIntGray(int pixel) {
        short gray = getGray(pixel);
        return combine(0xFF, gray, gray, gray);
    }

    public static int getBlank(int pixel) {
        return 0;
    }

    public static int normalize(int pixel) {
        return Maths.truncate(pixel, 0, 255);
    }

}
