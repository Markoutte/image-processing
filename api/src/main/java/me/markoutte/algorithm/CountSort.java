package me.markoutte.algorithm;

import me.markoutte.image.Edge;

import java.util.Arrays;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/06/05
 */
public final class CountSort {

    public enum Type {
        ARRAY_TO_COPY,
        CALC_FOR_SORT
    }

    public static void sort(Edge[] edges, Type type) {
        int max = 0;
        for (Edge edge : edges) {
            max = (int) Math.max(max, edge.getWeight());
        }
        switch (type) {
            case ARRAY_TO_COPY:
                sort(edges, max);
                break;
            case CALC_FOR_SORT:
                sort_(edges, max);
                break;
        }
    }

    private static void sort(Edge[] edges, int max) {
        Edge[] orig = Arrays.copyOf(edges, edges.length);
        int[] counts = new int[max + 1];
        // проход, считаем количество рёбер определённого веса
        for (Edge edge : orig) {
            counts[(int) edge.getWeight()]++;
        }
        // строим индексы, куда помещать элементы
        int[] indices = new int[max + 1];
        for (int i = 1; i < counts.length; i++) {
            indices[i] = indices[i - 1] + counts[i - 1];
        }
        int[] added = new int[max + 1];
        for (int i = 0; i < orig.length; i++) {
            int c = (int) orig[i].getWeight();
            edges[indices[c] + added[c]++] = orig[i];
        }
    }

    // версия сортировки без использования дополнительного массива (но работает дольше)
    private static void sort_(Edge[] edges, int max) {
        int[] counts = new int[max + 1];
        // проход, считаем количество рёбер определённого веса
        for (Edge edge : edges) {
            counts[(int) edge.getWeight()]++;
        }
        // строим индексы, куда помещать элементы
        int[] indices = new int[max + 1];
        for (int i = 1; i < counts.length; i++) {
            indices[i] = indices[i - 1] + counts[i - 1];
        }
        int[] added = new int[max + 1];
        // максимальное число операций, которые я видел, это 2 * edges.length,
        // но это стоило бы доказать точно, а не эмпирически
        for (int i = 0; i < edges.length;) {
            int c = (int) edges[i].getWeight();
            int idx = indices[c] + added[c]++;
            if (i != idx && added[c] <= counts[c]) {
                Edge tmp = edges[i];
                edges[i] = edges[idx];
                edges[idx] = tmp;
            } else {
                i++;
            }
        }
    }
    
    private CountSort() {
    }
}
