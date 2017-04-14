package me.markoutte.process.impl;

import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.process.ImageProcessing;

import java.util.Properties;

import static me.markoutte.ds.Color.getGray;

public enum HistogramProcessing implements ImageProcessing {

    OTSU {
        @Override
        public Image process(Image src, Properties properties) {
            int threshold = Color.getOtsuThreshold(src);
            Image out = src.clone();
            for (Pixel pixel : src) {
                out.setPixel(pixel.getId(), getGray(pixel.getValue()) < threshold ? 0xFF000000 : 0xFFFFFFFF);
            }
            return out;
        }
    },

    BINARIZATION {
        @Override
        public Image process(Image src, Properties properties) {
            int min = 255;
            int max = 0;
            for (Pixel pixel : src) {
                short gray = getGray(pixel.getValue());
                min = Math.min(gray, min);
                max = Math.max(gray, max);
            }

            int threshold = (max + min) / 2;
            Image out = src.clone();
            for (Pixel pixel : src) {
                out.setPixel(pixel.getId(), getGray(pixel.getValue()) < threshold ? 0xFF000000 : 0xFFFFFFFF);
            }
            return out;
        }
    },

    EQUALIZE {
        @Override
        public Image process(Image src, Properties properties) {
            final int range = 256;
            double red[] = new double[range];
            double green[] = new double[range];
            double blue[] = new double[range];
            for (Pixel pixel : src) {
                red[Color.getChannel(pixel.getValue(), Channel.RED)] += 1;
                green[Color.getChannel(pixel.getValue(), Channel.GREEN)] += 1;
                blue[Color.getChannel(pixel.getValue(), Channel.BLUE)] += 1;
            }
            for (int i = 0; i < range; i++) {
                red[i] = red[i] * 255 / src.getSize();
                green[i] = green[i] * 255 / src.getSize();
                blue[i] = blue[i] * 255 / src.getSize();
            }
            for (int i = 1; i < range; i++) {
                red[i] = red[i - 1] + red[i];
                green[i] = green[i - 1] + green[i];
                blue[i] = blue[i - 1] + blue[i];
            }
            Image out = src.clone();
            for (Pixel pixel : src) {
                out.setPixel(pixel.getId(), Color.combine(
                        255,
                        (int) red[Color.getChannel(pixel.getValue(), Channel.RED)],
                        (int) green[Color.getChannel(pixel.getValue(), Channel.GREEN)],
                        (int) blue[Color.getChannel(pixel.getValue(), Channel.BLUE)]
                ));
            }
            return out;
        }
    },

    ;

    @Override
    public String toString() {
        return name();
    }

}
