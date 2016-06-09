package me.markoutte.ds;

import java.util.Set;

public interface PersistentUnionFindSet {

    public void set(int id);

    public int union(int left, int right, double version);

    public int find(int id, double version);

    public int size(double version);

    public Set<Integer> segments(double version);

    public void compress();

}
