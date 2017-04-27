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

    public int[] data() {
        return parents;
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

    public void compress() {
        for (int i : parents) {
            parents[i] = find(i);
        }
    }
}
