package me.markoutte.image;

import java.io.Serializable;

public class Pixel implements Serializable {

    protected static final long serialVersionUID = 1L;

    int id;
    int value;

    public Pixel() {
        ;
    };

    public Pixel(int id) {
        this();
        this.id = id;
    }

    public Pixel(int id, int value) {
        this(id);
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}

