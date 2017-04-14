package me.markoutte.algorithm;

import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;

public enum ColorHeuristics implements Heuristics {
    MEAN {
        @Override
        public double getWeight(int left, int right) {
            int redDiff = Math.abs(Color.getChannel(left, Channel.RED) - Color.getChannel(right, Channel.RED));
            int greenDiff = Math.abs(Color.getChannel(left, Channel.GREEN) - Color.getChannel(right, Channel.GREEN));
            int blueDiff = Math.abs(Color.getChannel(left, Channel.BLUE) - Color.getChannel(right, Channel.BLUE));

            return Math.ceil((redDiff + greenDiff + blueDiff)/3.0);
        }
    },

    HUE {
        @Override
        public double getWeight(int left, int right) {
            return Math.abs(Color.getHSL(left).getHue() - Color.getHSL(right).getHue()) * 255;
        }
    },

    SATURATION {
        @Override
        public double getWeight(int left, int right) {
            return Math.abs(Color.getHSL(left).getSaturation() - Color.getHSL(right).getSaturation()) * 255;
        }
    },

    GRAYSCALE {
        @Override
        public double getWeight(int left, int right) {
            return Math.abs(Color.getHSL(left).getSaturation() - Color.getHSL(right).getSaturation()) * 255;
        }
    },;


    @Override
    public String toString() {
        return name();
    }
}
