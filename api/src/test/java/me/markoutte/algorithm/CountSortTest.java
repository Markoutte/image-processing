package me.markoutte.algorithm;

import me.markoutte.image.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/06/05
 */
public class CountSortTest {

    private RectImage image = Images.LENNA_512.toImage();

    @Test
    public void sort() throws Exception {

        Edge[] edges = {
                new Edge(0, 0, 3),
                new Edge(0, 0, 1),
                new Edge(0, 0, 5),
                new Edge(0, 0, 2),
                new Edge(0, 0, 4),
                new Edge(0, 0, 7),
                new Edge(0, 0, 3),
                new Edge(0, 0, 4),
                new Edge(0, 0, 8),
        };

        CountSort.sort(edges, CountSort.Type.ARRAY_TO_COPY);
        for (int i = 1; i < edges.length; i++) {
            Assert.assertTrue(edges[i - 1].getWeight() <= edges[i].getWeight());
        }
    }

    @Test
    public void count_sort_with_array() throws Exception {
        Edge[] edges = calculateEdges();
        CountSort.sort(edges, CountSort.Type.ARRAY_TO_COPY);
        for (int i = 1; i < edges.length; i++) {
            Assert.assertTrue(edges[i - 1].getWeight() <= edges[i].getWeight());
        }
    }

    @Test
    public void count_sort_without_array() throws Exception {
        Edge[] edges = calculateEdges();
        CountSort.sort(edges, CountSort.Type.CALC_FOR_SORT);
        for (int i = 1; i < edges.length; i++) {
            Assert.assertTrue(edges[i - 1].getWeight() <= edges[i].getWeight());
        }
    }

    private Edge[] calculateEdges() {
        int edgeCount = 2*image.getSize() - image.width() - image.height();
        Edge[] edges = new Edge[edgeCount];
        int edgesAdded = 0;
        for (Pixel i : image) {
            if (i.getId() % image.width() != (image.width() - 1)) {
                Pixel left = east(i);
                Edge e = new Edge(i.getId(), left.getId(), ColorHeuristics.MEAN.getWeight(i.getValue(), left.getValue()));
                edges[edgesAdded] = e;
                edgesAdded++;
            }
            if (i.getId() / image.width() != (image.height() - 1)) {
                Pixel top = south(i);
                Edge e = new Edge(i.getId(), top.getId(), ColorHeuristics.MEAN.getWeight(i.getValue(), top.getValue()));
                edges[edgesAdded] = e;
                edgesAdded++;
            }
        }
        return edges;
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