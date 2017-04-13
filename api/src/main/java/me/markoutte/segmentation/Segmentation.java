package me.markoutte.segmentation;

import me.markoutte.algorithm.Heuristics;
import me.markoutte.ds.Hierarchy;
import me.markoutte.image.Image;

public interface Segmentation<T extends Image> {

    void setImage(T image);

    void setHeuristic(Heuristics heuristic);

    T getImage();

    T getImage(int level);

    void start();

    Hierarchy getHierarchy();

}
