package me.markoutte.algorithm;

import me.markoutte.image.Edge;
import org.junit.Assert;
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
                new Edge(1, 0, 1),
                new Edge(2, 0, 5),
                new Edge(3, 0, 2),
                new Edge(4, 0, 4),
                new Edge(5, 0, 7),
                new Edge(6, 0, 3),
                new Edge(7, 0, 4),
                new Edge(8, 0, 8),
        };

        System.out.println(Arrays.toString(edges));
        CountSort.sort(edges, 9);
        System.out.println(Arrays.toString(edges));
        for (int i = 1; i < edges.length; i++) {
            Assert.assertTrue(edges[i - 1].getWeight() <= edges[i].getWeight());
            
        }
    }

}