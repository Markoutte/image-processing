package me.markoutte.benchmark;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.Image;
import me.markoutte.image.Images;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.image.impl.HashMapBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.NaiveFloodFill;
import org.openjdk.jmh.annotations.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/28
 */
@Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
public class AverageProcessingTime {

    @State(Scope.Benchmark)
    public static class ImageHolder {

        @Param({"LENNA_8", "LENNA_16", "LENNA_32", "LENNA_64", "LENNA_128", "LENNA_256", "LENNA_512"})
        public String name;
        public RectImage image;

        @Setup(Level.Trial)
        public void loadImage() {
            for (Images images : Images.values()) {
                if (Objects.equals(images.name(), name)) {
                    image = images.toImage();
                    return;
                }
            }
            throw new IllegalArgumentException("Cannot find image");
        }
    }

//    @Benchmark
//    public void runKruskal(ImageHolder holder) {
//        KruskalFloodFill ff = new KruskalFloodFill();
//        ff.setUseFastSort(false);
//        ff.setImage(holder.image);
//        ff.start();
//    }

//    @Benchmark
//    public void runFastKruskal(ImageHolder holder) {
//        KruskalFloodFill ff = new KruskalFloodFill();
//        ff.setImage(holder.image);
//        ff.start();
//    }

    @Benchmark
    public void runNaive(ImageHolder holder) {
        NaiveFloodFill ff = new NaiveFloodFill();
        ff.setImage(holder.image);
        ff.setBounds(0, 10);
        ff.start();
    }
//
//    @State(Scope.Benchmark)
//    public static class KruskalFloodFillSegmentation {
//
//        private KruskalFloodFill ff;
//
//        @Param({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64"})
//        public double level;
//
//        @Setup(Level.Iteration)
//        public void presegmentation() {
//            ff = new KruskalFloodFill();
//            ff.setImage(Images.LENNA_512.toImage());
//            ff.start();
//        }
//    }
//
//    @Benchmark
//    @Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
//    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
//    public void runHashMapImageRetriever(KruskalFloodFillSegmentation s) {
//        s.ff.setImageRetriever(new HashMapBasedImageRetriever());
//        s.ff.getImage(s.level, PseudoColorizeMethod.AVERAGE);
//    }
//
//    @Benchmark
//    @Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
//    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
//    public void runArrayBasedImageRetriever(KruskalFloodFillSegmentation s) {
//        s.ff.setImageRetriever(new ArrayBasedImageRetriever());
//        s.ff.getImage(s.level, PseudoColorizeMethod.AVERAGE);
//    }
}
