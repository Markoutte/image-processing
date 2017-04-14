package me.markoutte.image;

public final class HSL {

    private final double hue;
    private final double saturation;
    private final double intensity;

    public HSL(double hue, double saturation, double intensity) {
        this.hue = hue;
        this.saturation = saturation;
        this.intensity = intensity;
    }

    public double getHue() {
        return hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getIntensity() {
        return intensity;
    }
}
