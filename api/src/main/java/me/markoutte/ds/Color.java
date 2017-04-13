package me.markoutte.ds;

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

    public static int getIntGray(int pixel) {
        short gray = getGray(pixel);
        return (0xFF << 24) + (gray << 16) + (gray << 8) + gray;
    }

    public static int getBlank(int pixel) {
        return 0;
    }

}
