package me.markoutte.image;

public final class Pixel {

    private final int id;
    private final int value;

    public Pixel(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

}

