package me.markoutte.image.impl;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.ArrayUnionFindSet;
import me.markoutte.image.Image;
import me.markoutte.image.Segments;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.utils.FXImageUtils;
import me.markoutte.utils.MeasureUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/27
 */
public class ArraySegmentsTest {

    @Test
    public void test() {
        ArrayUnionFindSet ufs = new ArrayUnionFindSet(7);
        int[] data = ufs.data();
        data[0] = 2;
        data[1] = 4;
        data[2] = 2;
        data[3] = 2;
        data[4] = 4;
        data[5] = 5;
        data[6] = 5;
        Assert.assertEquals(3, ufs.size());

        ArraySegments segments = (ArraySegments) ArraySegments.from(ufs);
        Assert.assertArrayEquals(new int[] {2, 0, 3, 4, 1, 5, 6}, segments.data);
        Assert.assertArrayEquals(new int[]{2, 0, 3}, segments.pixels(0));
        Assert.assertArrayEquals(new int[]{4, 1}, segments.pixels(1));
        Assert.assertArrayEquals(new int[]{5, 6}, segments.pixels(2));
    }

    @Test
    public void testImageResult() throws IOException {
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(FXImageUtils.getDefaultImage());
        ff.setImageRetriever(new HashMapBasedImageRetriever());
        ff.start();

        List<Image> expected = new ArrayList<>();
        double[] bounds = ff.getBounds();
        MeasureUtils.Stopwatch timer1 = MeasureUtils.createTimer();
        for (double level = bounds[0] + 1; level < bounds[1]; level++) {
            expected.add(ff.getImage(level, PseudoColorizeMethod.AVERAGE));
        }
        timer1.stop(totalTime -> System.out.println(String.format("Total time for hashmap %dms", totalTime)));

        List<Image> actual = new ArrayList<>();
        MeasureUtils.Stopwatch timer2 = MeasureUtils.createTimer();
        ff.setImageRetriever(new ArrayBasedImageRetriever());
        for (double level = bounds[0] + 1; level < bounds[1]; level++) {
            actual.add(ff.getImage(level, PseudoColorizeMethod.AVERAGE));
        }
        timer2.stop(totalTime -> System.out.println(String.format("Total time for arrays %dms", totalTime)));

        for (int i = 0; i < expected.size(); i++) {
            Image e = expected.get(i);
            Image a = actual.get(i);
            Assert.assertArrayEquals(e.getBytes(), a.getBytes());
        }
    }
}