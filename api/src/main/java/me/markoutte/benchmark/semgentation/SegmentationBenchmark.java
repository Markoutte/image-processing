package me.markoutte.benchmark.semgentation;

import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.image.impl.HashMapBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.NaiveFloodFill;
import me.markoutte.segmentation.Segmentation;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static me.markoutte.utils.FXImageUtils.*;

/**
 * Pelevin Maksim <maks.pelevin@oogis.ru>
 *
 * @since 2017/04/28
 */
@Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
public class SegmentationBenchmark {
    
    private static final RectImage image = getDefaultImage();
    
    @Benchmark
    public void runKruskal() {
        KruskalFloodFill ff = new KruskalFloodFill();
        ff.setImage(image);
        ff.start();
    }

    @Benchmark
    public void runNaive() {
        NaiveFloodFill ff = new NaiveFloodFill();
        ff.setImage(image);
        ff.start();
    }
    
    @State(Scope.Benchmark)
    public static class KruskalFloodFillSegmentation {

        private KruskalFloodFill ff;

        @Param({"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64"})
        public double level;
        
        @Setup(Level.Iteration)
        public void presegmentation() {
            ff = new KruskalFloodFill();
            ff.setImage(image);
            ff.start();
        }
    }

    @Benchmark
    @Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void runHashMapImageRetriever(KruskalFloodFillSegmentation s) {
        s.ff.setImageRetriever(new HashMapBasedImageRetriever());
        s.ff.getImage(s.level, PseudoColorizeMethod.AVERAGE);
    }

    @Benchmark
    @Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void runArrayBasedImageRetriever(KruskalFloodFillSegmentation s) {
        s.ff.setImageRetriever(new ArrayBasedImageRetriever());
        s.ff.getImage(s.level, PseudoColorizeMethod.AVERAGE);
    }
}
