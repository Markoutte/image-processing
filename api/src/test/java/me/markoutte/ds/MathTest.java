package me.markoutte.ds;

import me.markoutte.algorithm.Maths;
import org.junit.Test;

import java.util.Arrays;

public class MathTest {

    @Test
    public void gaussian() {
        // see https://en.wikipedia.org/wiki/Gaussian_blur
        double[][] gaussian = Maths.gaussian(1.5, 3);
        for (int y = 0; y < gaussian.length; y++) {
            for (int x = 0; x < gaussian[y].length; x++) {
                System.out.print(String.format("%f ", gaussian[x][y]));
            }
            System.out.println();
        }
    }

}
