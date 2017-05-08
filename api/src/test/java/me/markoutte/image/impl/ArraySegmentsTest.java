package me.markoutte.image.impl;

import me.markoutte.benchmark.Strings;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.ds.impl.ArrayUnionFindSet;
import me.markoutte.image.Image;
import me.markoutte.image.Images;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.image.ImageHelpers;
import me.markoutte.benchmark.MeasurementUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.markoutte.benchmark.Strings.$;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/27
 */
public class ArraySegmentsTest {

    private static final Logger L = Logger.getLogger(ArraySegmentsTest.class);

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

        ArraySegments segments = (ArraySegments) ArraySegments.from_(ufs);
        Assert.assertArrayEquals(new int[] {2, 0, 3, 4, 1, 5, 6}, segments.data);
        Assert.assertArrayEquals(new int[]{2, 0, 3}, segments.pixels(0));
        Assert.assertArrayEquals(new int[]{4, 1}, segments.pixels(1));
        Assert.assertArrayEquals(new int[]{5, 6}, segments.pixels(2));
    }

    @Test
    public void testImageResult() throws IOException {
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(Images.LENNA_8.toImage());
        ff.setImageRetriever(new HashMapBasedImageRetriever());
        ff.start();

        List<Image> expected = new ArrayList<>();
        double[] bounds = ff.getBounds();
        MeasurementUtils.Stopwatch timer1 = MeasurementUtils.startStopwatch();
        for (double level = bounds[0] + 1; level < bounds[1]; level++) {
            expected.add(ff.getImage(level, PseudoColorizeMethod.AVERAGE));
        }
        timer1.stop(totalTime -> L.info($("Total time for hashmap {0}s", totalTime)));

        List<Image> actual = new ArrayList<>();
        MeasurementUtils.Stopwatch timer2 = MeasurementUtils.startStopwatch();
        ff.setImageRetriever(new ArrayBasedImageRetriever());
        for (double level = bounds[0] + 1; level < bounds[1]; level++) {
            actual.add(ff.getImage(level, PseudoColorizeMethod.AVERAGE));
        }
        timer2.stop(totalTime -> L.info($("Total time for arrays {0}s", totalTime)));

        for (int i = 0; i < expected.size(); i++) {
            Image e = expected.get(i);
            Image a = actual.get(i);
            Assert.assertArrayEquals(e.getBytes(), a.getBytes());
        }
    }
}
