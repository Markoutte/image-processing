package me.markoutte.ds.impl;

import me.markoutte.ds.*;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;
import me.markoutte.image.Segments;
import me.markoutte.utils.MeasureUtils;

import javax.swing.text.Segment;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public PersistentUnionFindSet getUfs() {
        return ufs;
    }

    @Override
    public int getSegment(int id, double level) {
        return ufs.find(id, level);
    }

    @Override
    public double[] getLevelBounds() {
        return new double[] {0.0, ufs.getCurrentValue()};
    }

    public void optimize() {
        ufs.compress();
    }
}
