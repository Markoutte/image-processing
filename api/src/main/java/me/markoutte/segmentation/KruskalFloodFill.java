package me.markoutte.segmentation;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.algorithm.Quicksort;
import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;
import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.UfsHierarchy;
import me.markoutte.image.Edge;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KruskalFloodFill implements Segmentation<RectImage> {

    private RectImage image = null;
    private Hierarchy hierarchy = null;
    private Heuristics heuristics = ColorHeuristics.MEAN;
    private ImageRetriever retriever = new ArrayBasedImageRetriever();

    private Edge[] edges;

    public RectImage getImage(double level, PseudoColorizeMethod colorize) {
        return (RectImage) retriever.getImage(((UfsHierarchy) hierarchy).getUfs(), level, colorize);
    }

    @Override
    public Map<Integer, List<Pixel>> getSegmentsWithValues(double level) {
        return retriever.getSegments(((UfsHierarchy) hierarchy).getUfs(), level);
    }

    public void setImage(RectImage image) {
        this.image = image;
        int edgeCount = 2*image.getSize() - image.width() - image.height();
        edges = new Edge[edgeCount];
        hierarchy = new UfsHierarchy();
        hierarchy.setImage(image);
        retriever.setImage(image);
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

    @Override
    public RectImage getImage() {
        return image;
    }

    public void start() {
        Objects.requireNonNull(edges);
        
        calculateEdges();
        new Quicksort<Edge>().sort(edges);
        calculateHierarchy();
        ((UfsHierarchy) hierarchy).optimize();
        edges = null;
    }

    @Override
    public double[] getBounds() {
        return hierarchy.getLevelBounds();
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
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

    private double getWeight(int left, int right) {
        return heuristics.getWeight(left, right);
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
}

