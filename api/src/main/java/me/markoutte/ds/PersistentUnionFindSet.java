package me.markoutte.ds;

import java.util.Set;

public interface PersistentUnionFindSet {

    void set(int id);

    int union(int left, int right, double version);

    int find(int id, double version);

    int size(double version);

    Set<Integer> segments(double version);

    void compress();

    /**
     * Возвращает неперсистентую систему для выбранного множества
     * @param version версия
     */
    UnionFindSet simplify(double version);

}
