package me.markoutte.process;

import me.markoutte.image.Image;

public interface ImageProcessing {

    <T extends Image> T process(T src);

}
