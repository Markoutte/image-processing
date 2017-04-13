package me.markoutte.ds;

import static me.markoutte.ds.Channel.*;

public final class Color {

    public static int getChannel(int pixel, Channel channel) {
        switch (channel) {
            case OPACITY:
                return (pixel & 0xFF000000) >> 24;
            case RED:
                return (pixel & 0x00FF0000) >> 16;
            case GREEN:
                return (pixel & 0x0000FF00) >> 8;
            case BLUE:
                return (pixel & 0x000000FF);
        }
        throw new IllegalStateException();
    }

    public static short getGray(int pixel) {
        return (short) (0.2126 * getChannel(pixel, Channel.RED) + 0.7252 * getChannel(pixel, Channel.GREEN) + 0.0722 * getChannel(pixel, Channel.BLUE));
    }

    public static int getIntGray(int pixel) {
        short gray = getGray(pixel);
        return (255 << 24) + (gray << 16) + (gray << 8) + gray;
    }

}
