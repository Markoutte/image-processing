package me.markoutte.process.impl;

import me.markoutte.algorithm.Maths;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.process.ImageProcessing;

import java.util.Properties;

import static me.markoutte.ds.Color.*;

public enum ColorProcessing implements ImageProcessing {

    GRAYSCALE {
        @Override
        public Image process(Image src, Properties properties) {
            Image clone = src.clone();
            for (Pixel pixel : src) {
                clone.setPixel(pixel.getId(), getIntGray(getGray(pixel.getValue())));
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

            return filter((RectImage) src, filter, false);
        }
    },

    RED {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), combine(255, getChannel(pixel.getValue(), Channel.RED), 0, 0));
            }
            return out;
        }
    },

    GREEN {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), combine(255, 0, getChannel(pixel.getValue(), Channel.GREEN), 0));
            }
            return out;
        }
    },

    BLUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), combine(255, 0, 0, getChannel(pixel.getValue(), Channel.BLUE)));
            }
            return out;
        }
    },

    HUE {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : src) {
                double hue = getHSL(pixel.getValue()).getHue();
                out.setPixel(pixel.getId(), getIntGray((int) (255 * hue)));
            }
            return out;
        }
    },

    SATURATION {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double saturation = getHSL(pixel.getValue()).getSaturation();
                out.setPixel(pixel.getId(), getIntGray((int) (255 * saturation)));
            }
            return out;
        }
    },

    INTENSITY {
        @Override
        public Image process(Image src, Properties properties) {
            Image out = src.clone();
            for (Pixel pixel : out) {
                double lightness = getHSL(pixel.getValue()).getIntensity();
                out.setPixel(pixel.getId(), getIntGray((int) (255 * lightness)));
            }
            return out;
        }
    },

    DIFF {
        @Override
        public Image process(Image src, Properties properties) {
            Image other = (Image) properties.get("DIFF.image");
            Image out = src.clone();
            for (Pixel pixel1 : out) {
                int red1 = getRed(pixel1.getValue());
                int green1 = getGreen(pixel1.getValue());
                int blue1 = getBlue(pixel1.getValue());

                int pixel2 = other.getPixel(pixel1.getId());
                int red2 = getRed(pixel2);
                int green2 = getGreen(pixel2);
                int blue2 = getBlue(pixel2);

                int dRed = normalize(red2 - red1);
                int dGreen = normalize(green2 - green1);
                int dBlue = normalize(blue2 - blue1);

                out.setPixel(pixel1.getId(), combine(255, dRed, dGreen, dBlue));
            }
            return out;
        }
    },

    WHITE_BALANCE {
        @Override
        public Image process(Image src, Properties properties) {
            int red = 0;
            int green = 0;
            int blue = 0;
            int gray = 0;
            for (Pixel pixel : src) {
                red += getRed(pixel.getValue());
                green += getGreen(pixel.getValue());
                blue += getBlue(pixel.getValue());
                gray += getGray(pixel.getValue());
            }
            red /= src.getSize();
            green /= src.getSize();
            blue /= src.getSize();
            gray /= src.getSize();
            Image out = src.clone();
            for (Pixel pixel : out) {
                out.setPixel(pixel.getId(), combine(
                        255,
                        normalize(getRed(pixel.getValue()) * gray / red),
                        normalize(getGreen(pixel.getValue()) * gray / green),
                        normalize(getBlue(pixel.getValue()) * gray / blue))
                );
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
