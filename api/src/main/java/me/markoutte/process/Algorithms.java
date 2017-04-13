package me.markoutte.process;

import me.markoutte.ds.Color;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;

public enum Algorithms implements ImageProcessing {

    GRAYSCALE {
        @Override
        public Image process(Image src) {
            Image clone = src.clone();
            for (Pixel pixel : src) {
                clone.setPixel(pixel.getId(), Color.getIntGray(pixel.getValue()));
            }
            return clone;
        }
    }

    ;
}
