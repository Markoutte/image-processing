package me.markoutte.algorithm;

/**
 * Набор правил для получения оценки разницы между 2-мя растрами
 */
public interface Heuristics {

    int getWeight(int left, int right);

}
