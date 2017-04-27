package me.markoutte.image.impl;

import me.markoutte.ds.impl.ArrayUnionFindSet;
import me.markoutte.image.Segments;
import org.junit.Assert;
import org.junit.Test;

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

        Segments segments = ArraySegments.from(ufs);
        System.out.println("breakpoint");
    }
    
}
