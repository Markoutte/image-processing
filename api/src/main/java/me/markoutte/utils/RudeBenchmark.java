package me.markoutte.utils;

import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.Image;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.image.impl.HashMapBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.NaiveFloodFill;
import me.markoutte.segmentation.Segmentation;

import java.io.IOException;
import java.io.InputStream;

public class RudeBenchmark {

    public static void main(String[] args) throws Exception {
        RectImage lena = FXImageUtils.getDefaultImage();
        Class<? extends Segmentation> segmentation = NaiveFloodFill.class;
        Class<? extends ImageRetriever> retriever = ArrayBasedImageRetriever.class;

        class Creator {
            Segmentation create() throws Exception {
                Segmentation ff = segmentation.newInstance();
                ff.setImage(lena);
                ff.setImageRetriever(retriever.newInstance());
                return ff;
            }
        }

        System.gc();
        Creator creator = new Creator();

        System.out.println("Segmentation: " + segmentation.getSimpleName());
        System.out.println("Image Retriever: " + retriever.getSimpleName());

        // warm up
        for (int i = 0; i < 3; i++) {
            creator.create().start();
        }

        MeasureUtils.Stopwatch total = MeasureUtils.createTimer();

        MeasureUtils.Stopwatch timer = MeasureUtils.createTimer();
        Segmentation s = creator.create();
        s.start();
        timer.stop(totalTime -> System.out.println(String.format("Hierarchy is built in %dms", totalTime)));

        double[] bounds = s.getBounds();
        MeasureUtils.Stopwatch stopwatch = MeasureUtils.createTimer();
        for (double i = bounds[0] + 1; i < bounds[1]; i++) {
            Image image = s.getImage(i, PseudoColorizeMethod.PLAIN);
        }
        stopwatch.stop(totalTime -> System.out.println(String.format("Total time for all levels %dms", totalTime)));

        total.stop(totalTime -> System.out.println(String.format("Processing time is %d", totalTime)));
    }

}
