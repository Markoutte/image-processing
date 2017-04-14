package me.markoutte.process.impl;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.process.ImageProcessing;

import java.util.Properties;

public enum ColorProcessing implements ImageProcessing {

    GRAYSCALE {
        @Override
        public Image process(Image src, Properties properties) {
            Image clone = src.clone();
            for (Pixel pixel : src) {
                clone.setPixel(pixel.getId(), Color.getIntGray(Color.getGray(pixel.getValue())));
            }
            return clone;
        }
    },

    BOX {
        @Override
        public RectImage process(Image src, Properties properties) {
            Integer kernel = Integer.valueOf(properties.getProperty("BOX.kernel", "3"));
            double[][] filter = new double[kernel][kernel];
            for (int j = 0; j < filter[0].length; j++) {
                for (int i = 0; i < filter.length; i++) {
                    filter[i][j] = 1d / kernel / kernel;
                }
            }

            return filter((RectImage) src, filter);
        }
    },

    RED {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, Color.getChannel(pixel.getValue(), Channel.RED), 0, 0));
            }
            return out;
        }
    },

    GREEN {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, 0, Color.getChannel(pixel.getValue(), Channel.GREEN), 0));
            }
            return out;
        }
    },

    BLUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), Color.combine(255, 0, 0, Color.getChannel(pixel.getValue(), Channel.BLUE)));
            }
            return out;
        }
    },

    HUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : src) {
                double hue = Color.getHSL(pixel.getValue()).getHue();
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * hue)));
            }
            return out;
        }
    },

    SATURATION {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double saturation = Color.getHSL(pixel.getValue()).getSaturation();
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * saturation)));
            }
            return out;
        }
    },

    INTENSITY {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double lightness = Color.getHSL(pixel.getValue()).getIntensity();
                out.setPixel(pixel.getId(), Color.getIntGray((int) (255 * lightness)));
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