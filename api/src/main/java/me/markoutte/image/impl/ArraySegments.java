package me.markoutte.image.impl;

import me.markoutte.ds.UnionFindSet;
import me.markoutte.ds.impl.ArrayUnionFindSet;
import me.markoutte.image.Segments;

import java.util.Arrays;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/27
 */
public class ArraySegments implements Segments {
    
    /* package private */ int[] pos;
    /* package private */ int[] data;

    public ArraySegments(int segments, int size) {
        pos = new int[segments];
        data = new int[size];
    }

    @Override
    public int size() {
        return pos.length;
    }

    @Override
    public int[] roots() {
        int[] roots = new int[size()];
        for (int i = 0; i < pos.length; i++) {
            int p = pos[i];
            roots[i] = data[p - 1];
        }
        return roots;
    }

    @Override
    public int[] pixels(int id) {
        
        return new int[0];
    }

    public static Segments from(ArrayUnionFindSet ufs) {
        // 1 прогон по массиву
        int count = ufs.size();
        int[] data = Arrays.copyOf(ufs.data(), ufs.data().length);
        int[] parents = new int[count];
        ArraySegments s = new ArraySegments(count, data.length);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (int i = 0, parno = 0; i < data.length; i++) {
            int parent = ufs.find(ufs.data()[i]);
            if (data[parent] == parent) {
                // Вместо ссылки на себя, указываем место в позиции маленького массива (с отрицательным значением).
                // -1 потому что начинается с 0
                data[parent] = -parno - 1;
                parents[parno] = parent;
                s.pos[parno]++;
                parno++;
            } else {
                s.pos[-data[parent] + 1]++;
            }
        }

        // segments.pos на текущий момент содержит площадь каждого из сегментов
        // будем сохранять информацию по положении каждого пикселя на своём месте
        int[] pixno = new int[count];
        for (int i = 0; i < s.pos.length - 1; i++) {
            int position = 1;
            if (i != 0) {
                position = s.pos[i - 1] + s.pos[i];
                s.pos[i] = position;
            }
            s.data[position - 1] = parents[i];
            pixno[i]++;
        }

        // 3 прогон
        for (int i = 0; i < data.length; i++) {
            int parent = data[i];
            if (parent >= 0) {
                int pos = -data[parent];
                int start = s.pos[pos];
                int shift = pixno[pos];
                s.data[start + shift] = i;
                pixno[pos]++;
            }
        }
        
        return s;
    }

    private static void exchange(int[] input, int i, int j) {
        if (input[i] != input[j]) {
            input[i] = input[i] ^ input[j];
            input[j] = input[i] ^ input[j];
            input[i] = input[i] ^ input[j];
        }
    }
}
