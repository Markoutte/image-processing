package me.markoutte.image.processing.ui;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;
import me.markoutte.image.RectImage;
import me.markoutte.segmentation.KruskalFloodFill;
import me.markoutte.segmentation.Segmentation;

public final class Configuration {

    public static final Heuristics heuristics = ColorHeuristics.MEAN;

    public static final Class<? extends Segmentation<RectImage>> segmentation = KruskalFloodFill.class;

    private Configuration() {
    }
}
