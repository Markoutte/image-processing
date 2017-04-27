package me.markoutte.image.impl;

import me.markoutte.ds.*;
import me.markoutte.image.Image;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapBasedImageRetriever implements ImageRetriever {

    private Image image;

    @Override
    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public Image getImage(PersistentUnionFindSet ufs, double level, PseudoColorizeMethod colorize) {
        if (level == 0) {
            return image;
        }

        Image image = this.image.clone();
        if (colorize == PseudoColorizeMethod.PLAIN) {
            Map<Integer, List<Integer>> segments = ufs.segments(level);
            for (List<Integer> segment : segments.values()) {
                int value = this.image.getPixel(segment.get(0));
                for (Integer integer : segment) {
                    image.setPixel(integer, value);
                }
            }

            return image;
        }

        if (colorize == PseudoColorizeMethod.AVERAGE) {
            Map<Integer, List<Integer>> segments = ufs.segments(level);
            for (List<Integer> segment : segments.values()) {
                long red = 0;
                long green = 0;
                long blue = 0;
                for (Integer pixel : segment) {
                    int value = image.getPixel(pixel);
                    red += Color.getChannel(value, Channel.RED);
                    green += Color.getChannel(value, Channel.GREEN);
                    blue += Color.getChannel(value, Channel.BLUE);
                }
                for (Integer pixel : segment) {
                    long summary = 255 << 24 | (red / segment.size()) << 16 | (green / segment.size()) << 8 | (blue / segment.size());
                    image.setPixel(pixel, (int) (summary));
                }
            }
            return image;
        }

        throw new UnsupportedOperationException("UfsHierarchy doesn't support method " + colorize);
    }

    @Override
    public Map<Integer, List<Pixel>> getSegments(PersistentUnionFindSet ufs, double level) {
        Map<Integer, List<Pixel>> segmentsWithValues = new HashMap<>();

        Map<Integer, List<Integer>> segments = ufs.segments(level);
        for (Map.Entry<Integer, List<Integer>> entry : segments.entrySet()) {
            List<Pixel> pixels = new ArrayList<>(entry.getValue().size());
            for (Integer id : entry.getValue()) {
                pixels.add(new Pixel(id, image.getPixel(id)));
            }
            segmentsWithValues.put(entry.getKey(), pixels);
        }
        return segmentsWithValues;
    }
}
