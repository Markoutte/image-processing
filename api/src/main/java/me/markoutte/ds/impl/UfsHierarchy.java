package me.markoutte.ds.impl;

import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PersistentUnionFindSet;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.UnionFindSet;
import me.markoutte.image.Image;
import me.markoutte.image.Pixel;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        Image image = getSourceImage();
        if (colorize == PseudoColorizeMethod.PLAIN) {
            UnionFindSet ufs = this.ufs.simplify(level);
            Set<Integer> segments = ufs.segments();
            for (Integer segment : segments) {
                List<Pixel> area = getArea(segment, ufs);
                Pixel first = area.get(0);
                for (Pixel pixel : area) {
                    image.setPixel(pixel.getId(), first.getValue());
                }
            }

            return image;
        }

        if (colorize == PseudoColorizeMethod.AVERAGE) {
            // TODO Как-то так, но собирать надо по каналам
//            Set<Integer> segments = getSegments(level);
//            for (Integer segment : segments) {
//                List<Pixel> area = getArea(segment, (int) level);
//                long summary = 0;
//                for (Pixel pixel : area) {
//                    summary += pixel.getValue();
//                }
//                for (Pixel pixel : area) {
//                    image.setPixel(pixel.getId(), (int) (summary / area.size()));
//                }
//            }
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
        List<Pixel> pixels = new LinkedList<Pixel>();
        for (Pixel pixel : image) {
            if (ufs.find(pixel.getId(), level) == id) {
                pixels.add(pixel);
            }
        }
        return pixels;
    }

    public List<Pixel> getArea(int id, UnionFindSet ufs) {
        List<Pixel> pixels = new LinkedList<Pixel>();
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
        return ufs.segments(level);
    }

    public void optimize() {
        ufs.compress();
    }
}
