package me.markoutte.utils;

import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.Image;
import me.markoutte.image.RectImage;
import me.markoutte.segmentation.KruskalFloodFill;

import java.io.IOException;
import java.io.InputStream;

public class RudeBenchmark {

    public static void main(String[] args) throws IOException {
        RectImage lena = getLena();

        // warm up
        for (int i = 0; i < 3; i++) {
            KruskalFloodFill ff = new KruskalFloodFill();
            ff.setImage(lena);
            ff.start();
        }

        MeasureUtils.Stopwatch timer = MeasureUtils.createTimer();
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(lena);
        ff.start();
        timer.stop(totalTime -> System.out.println(String.format("Hierarchy is built in %dms", totalTime)));

        Hierarchy hierarchy = ff.getHierarchy();
        double[] bounds = hierarchy.getLevelBounds();
        MeasureUtils.Stopwatch stopwatch = MeasureUtils.createTimer();
        for (double i = bounds[0] + 1; i < bounds[1]; i++) {
            Image image = ff.getImage(i, PseudoColorizeMethod.PLAIN);
        }
        stopwatch.stop(totalTime -> System.out.println(String.format("Total time for all levels %dms", totalTime)));

    }

    private static RectImage getLena() throws IOException {
        try (InputStream stream = RudeBenchmark.class.getClassLoader().getResourceAsStream("me/markoutte/image/lena-color.jpg")) {
            return FXImageUtils.fromFXImage(new javafx.scene.image.Image(stream));
        }
    }

}
