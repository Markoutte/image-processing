package me.markoutte.ds.impl;

import me.markoutte.ds.*;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;

import javax.swing.text.Segment;
import java.util.*;
import java.util.stream.Collectors;

public final class UfsHierarchy implements Hierarchy {

    private Image image;
    private PersistentUnionFindSet ufs;

    public UfsHierarchy() {
    }

    @Override
    public void setImage(Image image) {
        this.image = image;
        ufs = new ArrayPersistentUnionFindSet(image.getSize());
    }

    @Override
    public Image getSourceImage() {
        return image;
    }

    @Override
    public void setImage(Image image, int level) {
        throw new UnsupportedOperationException("UfsHierarchy doesn't support random image setting");
    }

    @Override
    public Image getImage(double level, PseudoColorizeMethod colorize) {
        if (level == 0) {
            return image;
        }

        Image image = getSourceImage().clone();
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
    public int union(int segment1, int segment2, double level) {
        return ufs.union(segment1, segment2, level);
    }

    @Override
    public List<Pixel> getArea(int id, int level) {
        List<Pixel> pixels = new LinkedList<>();
        for (Pixel pixel : image) {
            if (ufs.find(pixel.getId(), level) == id) {
                pixels.add(pixel);
            }
        }
        return pixels;
    }

    public List<Pixel> getArea(int id, UnionFindSet ufs) {
        List<Pixel> pixels = new LinkedList<>();
        for (Pixel pixel : image) {
            if (ufs.find(pixel.getId()) == id) {
                pixels.add(pixel);
            }
        }
        return pixels;
    }

    @Override
    public int getSegment(int id, double level) {
        return ufs.find(id, level);
    }

    @Override
    public Set<Integer> getSegments(double level) {
        return Collections.unmodifiableSet(ufs.segments(level).keySet());
    }

    @Override
    public Map<Integer, List<Integer>> getSegmentsAsMap(double level) {
        return Collections.unmodifiableMap(ufs.segments(level));
    }

    @Override
    public double[] getLevelBounds() {
        return new double[] {0.0, ufs.getCurrentValue()};
    }

    public void optimize() {
        ufs.compress();
    }
}
