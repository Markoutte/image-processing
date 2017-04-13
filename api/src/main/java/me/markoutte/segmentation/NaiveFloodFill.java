package me.markoutte.segmentation;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.UfsHierarchy;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NaiveFloodFill implements Segmentation<RectImage> {

    private RectImage image = null;
    private Hierarchy hierarchy = new UfsHierarchy();
    private Heuristics heuristics = ColorHeuristics.MEAN;
    private int delta;
    private boolean[] painted;

    public NaiveFloodFill() {
        ;
    }

    public RectImage getImage(int level) {
        RectImage ret = (RectImage) hierarchy.getImage(level, PseudoColorizeMethod.PLAIN);
        return ret;
    }

    public void setImage(RectImage image) {
        this.image = image;
        hierarchy.setImage(image);
    }

    @Override
    public void setHeuristic(Heuristics heuristic) {
        this.heuristics = heuristic;
    }

    @Override
    public RectImage getImage() {
        return image;
    }

    public void start() {
        for (delta = 0; delta <= 255; delta++) {
            painted = new boolean[image.getSize()];
            for (Pixel px : image) {
                if (!painted[px.getId()]) {
                    fill(px);
                }
            }
        }
    }

    @Override
    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    ;

    private int getPixel(int id) {
        return image.getPixel(id);
    }

    private void fill(Pixel element) {
        if (isOutOfBounds(element) || painted[element.getId()]) {
            return;
        }

        Queue<Pixel> q = new LinkedBlockingQueue();

        q.offer(element);
        while (!q.isEmpty()) {
            Pixel n = q.poll();
            int c = n.getValue();
            Pixel west = west(n);
            if (west != null && examine(west, c)) {
                paint(west, n);
                q.offer(west);
            }
            Pixel east = east(n);
            if (east != null && examine(east, c)) {
                paint(east, n);
                q.offer(east);
            }
            Pixel north = north(n);
            if (north != null && examine(north, c)) {
                paint(north, n);
                q.offer(north);
            }
            Pixel south = south(n);
            if (south != null && examine(south, c)) {
                paint(south, n);
                q.offer(south);
            }
        }
    }

    private boolean isSimilar(int left, int right) {
        if (heuristics.getWeight(left, right) <= delta) {
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Pixel p) {
        if (p.getId() < 0 || p.getId() >= image.getSize())
            return true;
        return false;
    }

    private boolean examine(Pixel p, int color) {
        int rgb = p.getValue();
        return !painted[p.getId()] && isSimilar(rgb, color);
    }

    private void paint(Pixel p, Pixel n) {
        hierarchy.union(p.getId(), n.getId(), delta);
        painted[p.getId()] = true;
    }

    private Pixel west(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = (x - 1) < 0 || (x - 1) >= image.width();
        boolean isOutOfY = y < 0 || y >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        Pixel ret = new Pixel( y * image.width() + (x - 1));
        ret.setValue(image.getPixel(ret.getId()));
        return ret;

    }

    private Pixel east(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = (x + 1) < 0 || (x + 1) >= image.width();
        boolean isOutOfY = y < 0 || y >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        Pixel ret = new Pixel( y * image.width() + (x + 1));
        ret.setValue(image.getPixel(ret.getId()));
        return ret;
    }

    private Pixel north(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = x < 0 || x >= image.width();
        boolean isOutOfY = (y - 1) < 0 || (y - 1) >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        Pixel ret = new Pixel( (y - 1) * image.width() + x);
        ret.setValue(image.getPixel(ret.getId()));
        return ret;
    }

    private Pixel south(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = x < 0 || x >= image.width();
        boolean isOutOfY = (y + 1) < 0 || (y + 1) >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        Pixel ret = new Pixel( (y + 1) * image.width() + x);
        ret.setValue(image.getPixel(ret.getId()));
        return ret;
    }
}

