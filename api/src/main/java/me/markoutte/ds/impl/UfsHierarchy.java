package me.markoutte.ds.impl;

import me.markoutte.ds.*;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.Segments;

import javax.swing.text.Segment;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public Image getImage_(double level, PseudoColorizeMethod colorize) {
        if (level == 0) {
            return image;
        }

        Image image = getSourceImage().clone();
        if (colorize == PseudoColorizeMethod.AVERAGE) {
            Segments segments = ufs.segments_(level);
            for (int segment : segments.roots()) {
                long red = 0;
                long green = 0;
                long blue = 0;
                int[] pixels = segments.pixels(segment);
                for (int pixel : pixels) {
                    int value = image.getPixel(pixel);
                    red += Color.getChannel(value, Channel.RED);
                    green += Color.getChannel(value, Channel.GREEN);
                    blue += Color.getChannel(value, Channel.BLUE);
                }
                for (int pixel : pixels) {
                    long summary = 255 << 24 | (red / pixels.length) << 16 | (green / pixels.length) << 8 | (blue / pixels.length);
                    image.setPixel(pixel, (int) (summary));
                }
            }
            System.out.println("-----");
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
    public Map<Integer, List<Pixel>> getSegmentsWithValues(double level) {
        Map<Integer, List<Integer>> segments = ufs.segments(level);
        Map<Integer, List<Pixel>> segmentsWithValues = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : segments.entrySet()) {
            List<Pixel> pixels = new ArrayList<>(entry.getValue().size());
            for (Integer id : entry.getValue()) {
                pixels.add(new Pixel(id, image.getPixel(id)));
            }
            segmentsWithValues.put(entry.getKey(), pixels);
        }
        return segmentsWithValues;
    }

    @Override
    public double[] getLevelBounds() {
        return new double[] {0.0, ufs.getCurrentValue()};
    }

    public void optimize() {
        ufs.compress();
    }
}
