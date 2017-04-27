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

    /* package */  int[] roots;
    /* package */  int[] pos;
    /* package */ int[] data;

    public ArraySegments(int segments, int size) {
        roots = new int[segments];
        pos = new int[segments];
        data = new int[size];
    }

    @Override
    public int size() {
        return pos.length;
    }

    @Override
    public int root(int index) {
        return roots[index];
    }

    @Override
    public int[] pixels(int index) {
        int from = pos[index];
        int to = index >= pos.length - 1 ? data.length : pos[index + 1];
        return Arrays.copyOfRange(data, from, to);
    }

    public static Segments from(ArrayUnionFindSet ufs) {
        // 1 прогон по массиву
        int count = ufs.size();
        int[] data = Arrays.copyOf(ufs.data(), ufs.data().length);
        ArraySegments s = new ArraySegments(count, data.length);
        
        for (int i = 0, parno = 0; i < data.length; i++) {
            int parent = ufs.find(ufs.data()[i]);
            // Мы ещё ничего не меняли, так что надо бы это исправить
            if (data[parent] == parent) {
                // Вместо ссылки на себя, указываем место в позиции маленького массива (с отрицательным значением).
                // -1 потому что начинается с 0
                data[parent] = -parno - 1;
                s.roots[parno] = parent;
                s.pos[parno]++;
                parno++;
            } else {
                // иначе просто продолжаем считать количество точек
                s.pos[-data[parent] - 1]++;
            }
        }

        // посчитаем индексы всех корневых элементов в датасете и сразу положим туда идентификторы
        int[] pixno = new int[count];
        int summary = s.data.length;
        for (int i = s.pos.length - 1; i >= 0; i--) {
            summary = summary - s.pos[i];
            s.pos[i] = summary;
            s.data[summary] = s.roots[i];
            pixno[i]++;
        }

        // теперь заполняем сами данные
        for (int i = 0; i < data.length; i++) {
            int parent = data[i];
            if (parent >= 0) {
                int pos = -data[parent] - 1;
                s.data[s.pos[pos] + pixno[pos]] = i;
                pixno[pos]++;
            }
        }
        
        return s;
    }
}