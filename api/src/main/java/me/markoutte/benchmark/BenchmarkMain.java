package me.markoutte.benchmark;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.image.impl.HashMapBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.image.ImageHelpers;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/28
 */
public class BenchmarkMain {
    
    public static void main(String... args) throws IOException, RunnerException {
        
//        Main.main(args);

        RectImage image = ImageHelpers.LENA;
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(image);
        ff.setImageRetriever(new ArrayBasedImageRetriever());
        ff.start();
        System.setProperty("memory.objects.dump", "true");
        for (double i = ff.getBounds()[0]; i < ff.getBounds()[1]; i++) {
            ff.getImage(i, PseudoColorizeMethod.AVERAGE);
        }
    }
}