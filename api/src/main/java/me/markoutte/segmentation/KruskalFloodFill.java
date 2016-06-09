package me.markoutte.segmentation;

import me.markoutte.algorithm.Quicksort;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.UfsHierarchy;
import me.markoutte.image.Edge;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;

public class KruskalFloodFill {

    private RectImage image = null;
    private Hierarchy hierarchy = null;

    private Edge[] edges;

    public RectImage getImage(int level) {
        RectImage ret = (RectImage) hierarchy.getImage(level, PseudoColorizeMethod.PLAIN);
        return ret;
    }

    public void setImage(RectImage image) {
        this.image = image;
        hierarchy = new UfsHierarchy();
        hierarchy.setImage(image);
        int edgeCount = 2*image.getSize() - image.width() - image.height();
        edges = new Edge[edgeCount];
    }

    public void start() {
        calculateEdges();
        new Quicksort<Edge>().sort(edges);
        calculateHierarchy();
        ((UfsHierarchy) hierarchy).optimize();
        edges = null;
    }

    private void calculateHierarchy() {
        int edgesDrawn = 0;
        for (Edge e : edges) {
            if (edgesDrawn >= image.getSize())
                return;
            int result = hierarchy.union(e.getA(), e.getB(), (int) e.getWeight());
            if (result >= 0)
                edgesDrawn++;
        }
    }

    private void calculateEdges() {
        int edgesAdded = 0;
        for (Pixel i : image) {
            if (i.getId() % image.width() != (image.width() - 1)) {
                Pixel left = east(i);
                Edge e = new Edge(i.getId(), left.getId(), getWeight(i.getValue(), left.getValue()));
                edges[edgesAdded] = e;
                edgesAdded++;
            }
            if (i.getId() / image.width() != (image.height() - 1)) {
                Pixel top = south(i);
                Edge e = new Edge(i.getId(), top.getId(), getWeight(i.getValue(), top.getValue()));
                edges[edgesAdded] = e;
                edgesAdded++;
            }
        }
    }

    private int getWeight(int left, int right) {
        int redDiff = Math.abs(Color.getChannel(left, Channel.RED) - Color.getChannel(right, Channel.RED));
        int greenDiff = Math.abs(Color.getChannel(left, Channel.GREEN) - Color.getChannel(right, Channel.GREEN));
        int blueDiff = Math.abs(Color.getChannel(left, Channel.BLUE) - Color.getChannel(right, Channel.BLUE));

        return (int) Math.ceil((redDiff + greenDiff + blueDiff)/3.0);
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

