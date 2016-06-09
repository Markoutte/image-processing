package me.markoutte.algorithm;

public class Quicksort<T extends Comparable<T>>  {

    public void sort(T[] values) {
        // check for empty or null array
        if (values ==null || values.length==0){
            return;
        }
        int number = values.length;
        quicksort(values, 0, number - 1);
    }

    private static void quicksort(Comparable[] items, int a, int b) {
        int i = a;
        int j = b;

        Comparable x = items[(a+ b) / 2];
        Comparable h;

        do {
            while (items[i].compareTo(x) < 0) {
                i++;
            }
            while (items[j].compareTo(x) > 0) {
                j--;
            }
            if (i <= j) {
                h = items[i];
                items[i] = items[j];
                items[j] = h;
                i++;
                j--;
            }
        } while (i <= j);

        if (a < j) {
            quicksort(items, a, j);
        }
        if (i < b) {
            quicksort(items, i, b);
        }
    }
}
