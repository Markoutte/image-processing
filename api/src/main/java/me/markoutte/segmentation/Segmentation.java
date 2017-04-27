package me.markoutte.segmentation;

import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PseudoColorizeMethod;
import me.markoutte.image.Image;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;

import java.util.List;
import java.util.Map;

public interface Segmentation<T extends Image> {

    void setImage(T image);

    void setHeuristic(Heuristics heuristic);

    void setImageRetriever(ImageRetriever retriever);

    T getImage();

    T getImage(double level, PseudoColorizeMethod colorize);

    Map<Integer, List<Pixel>> getSegmentsWithValues(double level);

    void start();

    Hierarchy getHierarchy();

}
