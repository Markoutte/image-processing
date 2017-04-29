package me.markoutte.benchmark;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.image.impl.HashMapBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.image.ImageHelpers;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jol.info.GraphLayout;

import java.io.IOException;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/28
 */
public class BenchmarkMain {
    
    public static void main(String... args) throws IOException, RunnerException {
        
        Main.main(args);

        ImageRetriever[] retrievers = {new ArrayBasedImageRetriever(), new HashMapBasedImageRetriever()};
        RectImage image = ImageHelpers.LENA;
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(image);
        ff.start();

        System.setProperty("memory.objects.dump", "true");
        GraphLayout.parseInstance(BenchmarkMain.class);

        for (ImageRetriever retriever : retrievers) {
            ff.setImageRetriever(retriever);
            if (MeasurementUtils.isDumpObjectSize()) {
                System.out.println(String.format("%-6s %-10s %10s %30s", "Level", "Total size", "Time (ms)", retriever.getClass().getSimpleName()));
                System.out.println("====================");
            }
            for (double i = ff.getBounds()[0]; i < ff.getBounds()[1]; i++) {
                ff.getImage(i, PseudoColorizeMethod.AVERAGE);
            }
            System.out.println("\n\n\n");
        }
    }
}