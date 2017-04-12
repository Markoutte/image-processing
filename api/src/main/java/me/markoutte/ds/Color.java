package me.markoutte.ds;

public final class Color {

    public static int getChannel(int pixel, Channel channel) {
        int color = 0;
        switch (channel) {
            case RED:
                color = (pixel & 0x00FF0000) >> 16;
                break;
            case GREEN:
                color = (pixel & 0x0000FF00) >> 8;
                break;
            case BLUE:
                color = (pixel & 0x000000FF);
                break;
            default:
        }
        return color;
    }

    public static int getGray(int pixel) {
        return (int) (0.21 * getChannel(pixel, Channel.RED) + 0.72 * getChannel(pixel, Channel.GREEN) + 0.07 * getChannel(pixel, Channel.BLUE));
    }

}
