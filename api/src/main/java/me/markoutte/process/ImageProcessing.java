package me.markoutte.process;

import me.markoutte.image.Image;

import java.util.Properties;

public interface ImageProcessing {

    Image process(Image src, Properties properties);

}
