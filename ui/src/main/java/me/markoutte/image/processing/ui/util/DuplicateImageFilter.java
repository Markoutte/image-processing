package me.markoutte.image.processing.ui.util;

import me.markoutte.image.processing.ui.components.ImageCanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class DuplicateImageFilter implements Function<List<ImageCanvas.Info>, List<ImageCanvas.Info>> {

    /**
     * Предел прироста сегмента в процентах
     */
    private final double increase;

    public DuplicateImageFilter() {
        this(0.01);
    }

    public DuplicateImageFilter(double increase) {
        this.increase = increase;
    }

    @Override
    public List<ImageCanvas.Info> apply(List<ImageCanvas.Info> result) {
        List<ImageCanvas.Info> filteredResult = new ArrayList<>(result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            ImageCanvas.Info current = result.get(i);
            ImageCanvas.Info next = result.get(i + 1);

            List<ImageCanvas.Info> sameImagesButUsedBefore = new ArrayList<>();
            for (int j = filteredResult.size() - 1; j >=0 && (next.getSize() - filteredResult.get(j).getSize()) / (double) filteredResult.get(j).getSize() < increase; j--) {
                if (Objects.equals(current.getSegmentId(), filteredResult.get(j).getSegmentId())) {
                    sameImagesButUsedBefore.add(filteredResult.get(j));
                }
            }
            filteredResult.removeAll(sameImagesButUsedBefore);

            if (Objects.equals(current.getSegmentId(), next.getSegmentId())) {
                int currentSize = current.getSize();
                int nextSize = next.getSize();
                if ((double) (nextSize - currentSize) / currentSize > increase) {
                    filteredResult.add(current);
                }
            } else {
                filteredResult.add(current);
            }
        }
        return filteredResult;
    }
}
