package me.markoutte.segmentation;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.UfsHierarchy;
import me.markoutte.image.Image;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.HashMapBasedImageRetriever;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class NaiveFloodFill implements Segmentation<RectImage> {

    private RectImage image = null;
    private Hierarchy hierarchy = new UfsHierarchy();
    private Heuristics heuristics = ColorHeuristics.MEAN;
    private ImageRetriever retriever = new HashMapBasedImageRetriever();
    private int delta;
    private int deltaMin = 0;
    private int deltaMax = 255;
    private boolean[] painted;

    public RectImage getImage(double level, PseudoColorizeMethod colorize) {
        return (RectImage) retriever.getImage(((UfsHierarchy) hierarchy).getUfs(), level, colorize);
    }

    @Override
    public Map<Integer, List<Pixel>> getSegmentsWithValues(double level) {
        return retriever.getSegments(((UfsHierarchy) hierarchy).getUfs(), level);
    }

    public void setImage(RectImage image) {
        this.image = image;
        hierarchy.setImage(image);
        retriever.setImage(image);
        painted = new boolean[image.getSize()];
    }

    @Override
    public void setHeuristic(Heuristics heuristic) {
        this.heuristics = heuristic;
    }

    @Override
    public void setImageRetriever(ImageRetriever retriever) {
        this.retriever = retriever;
        this.retriever.setImage(image);
    }

    public void setRetriever(ImageRetriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public RectImage getImage() {
        return image;
    }

    public void start() {
        for (delta = deltaMin; delta <= deltaMax; delta++) {
            if (((UfsHierarchy) hierarchy).getUfs().size(delta) == 1) {
                break;
            }

            painted = new boolean[image.getSize()];
            for (Pixel px : image) {
                if (!painted[px.getId()]) {
                    fill(px);
                }
            }
        }
    }

    @Override
    public double[] getBounds() {
        return hierarchy.getLevelBounds();
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

        Queue<Pixel> q = new ArrayDeque<>();

        q.offer(element);
        painted[element.getId()] = true;
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
        int id = y * image.width() + (x - 1);
        return new Pixel(id, image.getPixel(id));

    }

    private Pixel east(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = (x + 1) < 0 || (x + 1) >= image.width();
        boolean isOutOfY = y < 0 || y >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        int id = y * image.width() + (x + 1);
        return new Pixel(id, image.getPixel(id));
    }

    private Pixel north(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = x < 0 || x >= image.width();
        boolean isOutOfY = (y - 1) < 0 || (y - 1) >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        int id = (y - 1) * image.width() + x;
        return new Pixel(id, image.getPixel(id));
    }

    private Pixel south(Pixel p) {
        int x = p.getId() % image.width();
        int y = p.getId() / image.width();
        boolean isOutOfX = x < 0 || x >= image.width();
        boolean isOutOfY = (y + 1) < 0 || (y + 1) >= image.height();
        if (isOutOfX || isOutOfY)
            return null;
        int id = (y + 1) * image.width() + x;
        return new Pixel(id, image.getPixel(id));
    }

    public void setBounds(int deltaMin, int deltaMax) {
        this.deltaMin = deltaMin;
        this.deltaMax = deltaMax;
    }

    // TESTING

    public static void testCenter(RectImage image, int maxValue) {
        NaiveFloodFill ff = new NaiveFloodFill();
        ff.setImage(image);
        ff.setBounds(maxValue + 1, maxValue + 1);
        int x = image.width() / 2;
        int y = image.height() / 2;
        ff.fill(new Pixel(y * image.width() + x, image.getPixel(x, y)));
    }

    private void drawImage(Queue<Pixel> pixels, int width, int height) {
        System.out.println("= " + pixels.size() + " =");
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                char draw = '·';
                for (Pixel pixel : pixels) {
                    if (pixel.getId() == j * width + i)
                        draw = '+';
                }
                System.out.print(draw);
            }
            System.out.println();
        }
        System.out.println();
    }
}

