package me.markoutte.algorithm;

import me.markoutte.image.Edge;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/06/05
 */
public class CountSortTest {
    
    
    @Test
    public void sort() throws Exception {

        Edge[] edges = {
                new Edge(0, 0, 3),
                new Edge(0, 0, 1),
                new Edge(0, 0, 5),
                new Edge(0, 0, 2),
                new Edge(0, 0, 4),
                new Edge(0, 0, 7),
                new Edge(0, 0, 3),
                new Edge(0, 0, 4),
                new Edge(0, 0, 8),
        };

        Edge[] sort = CountSort.sort(edges, 9);
        System.out.println(Arrays.toString(edges));
    }

}