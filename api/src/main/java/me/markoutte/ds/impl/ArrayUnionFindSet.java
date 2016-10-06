package me.markoutte.ds.impl;

import me.markoutte.ds.UnionFindSet;

import java.util.*;

public class ArrayUnionFindSet implements UnionFindSet {

    /* package private */ int[] parents;

    public ArrayUnionFindSet(int size) {
        parents = new int[size];
        for (int i = 0; i < parents.length; i++) {
            parents[i] = i;
        }
    }

    public void set(int id) {
        parents[id] = id;
    }

    public int union(int left, int right) {
        left = find(left);
        right = find(right);
        if (left == right) {
            return -1;
        }
        parents[left] = right;
        return right;
    }

    public int find(int id) {
        while (id != parents[id]) {
            id = parents[id];
        }
        return id;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < parents.length; i++) {
            if (parents[i] == i) {
                size++;
            }
        }
        return size;
    }

    public Map<Integer, List<Integer>> segments() {
        Map<Integer, List<Integer>> segments = new HashMap<>();
        for (int i = 0; i < parents.length; i++) {
            int parent = find(i);
            List<Integer> set = segments.get(parent);
            if (set == null) {
                segments.put(parent, set = new ArrayList<>());
            }
            set.add(i);
        }
        return segments;
    }

    public void compress() {
        for (int i : parents) {
            parents[i] = find(i);
        }
    }
}
