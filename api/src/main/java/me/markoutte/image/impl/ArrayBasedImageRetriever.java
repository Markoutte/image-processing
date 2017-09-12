package me.markoutte.image.impl;

import me.markoutte.ds.*;
import me.markoutte.image.Image;
import me.markoutte.image.ImageRetriever;
import me.markoutte.image.Pixel;
import me.markoutte.image.Segments;
import me.markoutte.benchmark.MeasurementUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static me.markoutte.benchmark.Strings.$;

public class ArrayBasedImageRetriever implements ImageRetriever {

    private Image image;

    @Override
    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public Image getImage(PersistentUnionFindSet ufs, double level, PseudoColorizeMethod colorize) {
        if (level == 0) {
            return image;
        }

        Image image = this.image.clone();
        long start = System.currentTimeMillis();
        Segments segments = ArraySegments.from(ufs, level);
        MeasurementUtils.dumpObjectSize(segments, totalSize -> System.out.println(String.format("%-6s %10s %10s", (int) level, totalSize, System.currentTimeMillis() - start)));

        if (colorize == PseudoColorizeMethod.PLAIN) {
            for (int id = 0; id < segments.size(); id++) {
                int value = this.image.getPixel(segments.root(id));
                for (int integer : segments.pixels(id)) {
                    image.setPixel(integer, value);
                }
            }

            return image;
        }

        if (colorize == PseudoColorizeMethod.AVERAGE) {
            for (int i = 0; i < segments.size(); i++) {
                long red = 0;
                long green = 0;
                long blue = 0;
                int[] pixels = segments.pixels(i);
                for (int pixel : pixels) {
                    int value = image.getPixel(pixel);
                    red += Color.getChannel(value, Channel.RED);
                    green += Color.getChannel(value, Channel.GREEN);
                    blue += Color.getChannel(value, Channel.BLUE);
                }
                for (int pixel : pixels) {
                    long summary = 255 << 24 | (red / pixels.length) << 16 | (green / pixels.length) << 8 | (blue / pixels.length);
                    image.setPixel(pixel, (int) (summary));
                }
            }
            return image;
        }

        throw new UnsupportedOperationException("UfsHierarchy doesn't support method " + colorize);
    }

    @Override
    public Map<Integer, List<Pixel>> getSegments(PersistentUnionFindSet ufs, double level) {

        Segments segments = ArraySegments.from(ufs, level);

        return IntStream.range(0, segments.size()).parallel().boxed()
                .collect(Collectors.toMap(segments::root, i -> {
                    int[] indices = segments.pixels(i);
                    List<Pixel> pixels = new ArrayList<>(indices.length);
                    for (int id : indices) {
                        pixels.add(new Pixel(id, image.getPixel(id)));
                    }
                    return pixels;
                }));
    }
}
