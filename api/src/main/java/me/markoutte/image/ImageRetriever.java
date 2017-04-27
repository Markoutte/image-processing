package me.markoutte.image;

import me.markoutte.ds.Hierarchy;
import me.markoutte.ds.PersistentUnionFindSet;
import me.markoutte.ds.PseudoColorizeMethod;

import java.util.List;
import java.util.Map;

public interface ImageRetriever {

    void setImage(Image image);

    Image getImage();

    Image getImage(PersistentUnionFindSet ufs, double level, PseudoColorizeMethod method);

    Map<Integer, List<Pixel>> getSegments(PersistentUnionFindSet ufs, double level);

}
