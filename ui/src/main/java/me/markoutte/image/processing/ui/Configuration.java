package me.markoutte.image.processing.ui;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;
import me.markoutte.image.RectImage;
import me.markoutte.image.impl.ArrayBasedImageRetriever;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.Segmentation;

import java.util.List;
import java.util.function.Predicate;

public final class Configuration {

    public static final Heuristics heuristics = ColorHeuristics.HUE;

    public static final PseudoColorizeMethod colorize = PseudoColorizeMethod.AVERAGE;

    public static final Class<? extends Segmentation<RectImage>> segmentation = KruskalFloodFill.class;

    public static final Class<? extends ImageRetriever> retriever = ArrayBasedImageRetriever.class;

    private Configuration() {
    }

    public static Predicate<? super List<Pixel>> segments() {
        return value -> true;
    }
}
