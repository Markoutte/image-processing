package me.markoutte.ds;

import me.markoutte.algorithm.Maths;
import me.markoutte.image.HSL;
import me.markoutte.image.Pixel;

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
        return h / (2 * PI);
    }

    public static double getSaturation(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;
        return  1 - 3 * min(r, g, b) / (r + g + b);
    }

    public static double getIntensity(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;
        return (r + g + b) / 3.;
    }

    public static HSL getHSL(int pixel) {
        double r = getChannel(pixel, Channel.RED) / 255.;
        double g = getChannel(pixel, Channel.GREEN) / 255.;
        double b = getChannel(pixel, Channel.BLUE) / 255.;

        double max =  max(r, g, b);
        double min = min(r, g, b);
        double h = 0;
        double s = 0;
        double l = (max + min) / 2;

        if (max != min) {
            double d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else if (max == b) {
                h = (r - g) / d + 4;
            } else throw new IllegalStateException();
            h /= 6;
        }

        return new HSL(h, s, l);
    }

    public static int getOtsuThreshold(Iterable<Pixel> pixels) {
        int hist[] = new int[256];
        int min = 255;
        int max = 0;
        for (Pixel pixel : pixels) {
            short gray = getGray(pixel.getValue());
            min = Math.min(gray, min);
            max = Math.max(gray, max);
        }

        for (Pixel pixel : pixels) {
            hist[getGray(pixel.getValue()) - min]++;
        }

        int temp = 0, templ = 0;
        for (int i = 0; i < hist.length; i++) {
            temp += i * hist[i];
            templ += hist[i];
        }

        int alpha = 0, beta = 0, threshold=0;
        double maxSigma = -1;
        for(int i = 0; i < hist.length; i++) {
            alpha += i * hist[i];
            beta += hist[i];

            double w1 = (double)beta / templ;
            double a = (double)alpha / beta - (double)(temp - alpha) / (templ - beta);
            double sigma;
            sigma = w1 * (1 - w1) * a * a;

            if (sigma > maxSigma)
            {
                maxSigma = sigma;
                threshold = i;
            }
        }
        return threshold;
    }

    private static double max(double a, double b, double c) {
        return Math.max(Math.max(a, b), c);
    }

    private static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    private Color() {
    }
}
