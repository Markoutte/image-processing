package me.markoutte.image.impl;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.Images;
import me.markoutte.image.RectImage;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.NaiveFloodFill;
import org.junit.Assert;
import org.junit.Test;

public class AlgorithmEqTest {

    @Test
    public void testMe() {
        KruskalFloodFill ff1 = new KruskalFloodFill();
        ff1.setImage(Images.LENNA_512.toImage());
        ff1.start();

        NaiveFloodFill ff2 = new NaiveFloodFill();
        ff2.setImage(Images.LENNA_512.toImage());
        ff2.start();

        for (int i = 1; i < 10; i++) {
            RectImage image1 = ff1.getImage(i, PseudoColorizeMethod.AVERAGE);
            RectImage image2 = ff2.getImage(i, PseudoColorizeMethod.AVERAGE);
            Assert.assertArrayEquals("Got 2 different image for level " + i, image1.getBytes(), image2.getBytes());
        }
    }
}
