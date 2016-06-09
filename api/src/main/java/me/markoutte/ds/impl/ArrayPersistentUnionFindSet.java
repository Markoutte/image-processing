package me.markoutte.ds.impl;

import me.markoutte.ds.PersistentUnionFindSet;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class ArrayPersistentUnionFindSet implements PersistentUnionFindSet {

    double initVersion = 0.0;
    double currentVersion = 0.0;
    int[] parents = null;
    double[] versions = null;

    int[] heights = null;

    public ArrayPersistentUnionFindSet(int size) {
        initialize(size);
    }

    public ArrayPersistentUnionFindSet(int size, double initVersion) {
        initialize(size);
        Arrays.fill(versions, initVersion);
        this.initVersion = initVersion;
        currentVersion = initVersion;
    }

    /**
     * Инициализирует массивы размером size в классе.
     * Причина по которой не вызывается конструктуор this(size, 0)
     * в ArrayPersistentUnionFind(int size) в том, что в противном случае
     * пришлось бы тратить время на ненужную инициализацию массива нулями
     */
    private void initialize(int size) {
        parents = new int[size];
        for (int i = 0; i < size; i++) {
            parents[i] = i;
        }
        versions = new double[size];
        heights = new int[size];
        Arrays.fill(heights, 1);
    }

    @Override
    public void set(int id) {
        parents[id] = id;
        versions[id] = initVersion;
    }

    @Override
    public int union(int left, int right, double version) {
        left = find(left, version);
        right = find(right, version);
        // Поиск корней дерева теоретически решает проблему,
        // которая может возникнуть при поиске родителя сегмента
        // TODO доказать, что версия дерева в направлении корня увеличивается
        if (left == right)
            return -1;
        int newRoot;
        if (heights[left] < heights[right]) {
            parents[left] = right;
            versions[left] = version;
            heights[right] += heights[left];
            newRoot = right;
        } else {
            parents[right] = left;
            versions[right] = version;
            newRoot = left;
            heights[left] += heights[right];
        }
        currentVersion = version;
        return newRoot;
    }

    @Override
    public int find(int id, double version) {
        while (id != parents[id] && version >= versions[id]) {
            id = parents[id];
        };
        return id;
    }

    @Override
    public int size(double version) {
        int size = 0;
        for (int i = 0; i < parents.length; i++) {
            if (parents[i] == i || version < versions[i]) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Set<Integer> segments(double version) {
        Set<Integer> segments = new TreeSet<Integer>();
        // Да, в этом случае double лучше сравнивать так
        for (int i = 0; i < parents.length; i++) {
            if (parents[i] == i || version < versions[i]) {
                segments.add(i);
            }
        }
        return segments;
    }

    @Override
    public void compress() {
        for (int i : parents) {
            parents[i] = find(i, versions[i]);
        }
    }
}


