package me.markoutte.segmentation;

import me.markoutte.algorithm.ColorHeuristics;
import me.markoutte.algorithm.Heuristics;

public final class SegmentationConfiguration {

    public static final Heuristics heuristics = ColorHeuristics.MEAN;

    private SegmentationConfiguration() {
    }
}
