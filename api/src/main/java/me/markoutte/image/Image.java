package me.markoutte.image;

public interface Image extends Iterable<Pixel>, Cloneable {

    public Image clone();

    public void setBytes(int[] rgbArray);

    public int[] getBytes();

    public abstract int getPixel(int id);

    public abstract void setPixel(int id, int value);

    public abstract int getSize();

}

