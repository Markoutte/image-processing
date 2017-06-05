package me.markoutte.algorithm;

import me.markoutte.image.Edge;

import java.util.Arrays;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/06/05
 */
public final class CountSort {

    public static Edge[] sort(Edge[] edges, int max) {
        int[] counts = new int[max];
        // проход, считаем количество рёбер определённого веса
        for (Edge edge : edges) {
            counts[(int) edge.getWeight()]++;
        }
        // строим индексы, куда помещать элементы
        int[] indices = new int[max];
        for (int i = 1; i < counts.length; i++) {
            indices[i] = indices[i - 1] + counts[i - 1];
        }
        Edge[] sorted = new Edge[edges.length];
        int[] added = new int[max];
        for (int i = 0; i < edges.length; i++) {
            int c = (int) edges[i].getWeight();
            sorted[indices[c] + added[c]++] = edges[i];
        }
        return sorted;
    }
    
    private CountSort() {
    }
}
