package me.markoutte.utils;

import me.markoutte.benchmark.MeasurementUtils;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.Segmentation;

public class RudeBenchmark {

    public static void main(String[] args) throws Exception {
        RectImage lena = FXImageUtils.getDefaultImage();
        Class<? extends Segmentation> segmentation = KruskalFloodFill.class;
        Class<? extends ImageRetriever> retriever = ArrayBasedImageRetriever.class;

        class Creator {
            Segmentation create() throws Exception {
                Segmentation ff = segmentation.newInstance();
                ff.setImage(lena);
                ff.setImageRetriever(retriever.newInstance());
                ff.getImage(1, PseudoColorizeMethod.AVERAGE);
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

        MeasurementUtils.Stopwatch total = MeasurementUtils.startStopwatch();

        MeasurementUtils.Stopwatch timer = MeasurementUtils.startStopwatch();
        Segmentation s = creator.create();
        s.start();
        timer.stop(totalTime -> String.format("Hierarchy is built in %dms", totalTime));

        double[] bounds = s.getBounds();
        MeasurementUtils.Stopwatch stopwatch = MeasurementUtils.startStopwatch();
        for (double i = bounds[0] + 1; i < bounds[1]; i++) {
            s.getImage(i, PseudoColorizeMethod.PLAIN);
        }
        stopwatch.stop(totalTime -> String.format("Total time for all levels %dms", totalTime));

        total.stop(totalTime -> String.format("Processing time is %d", totalTime));
    }

}
