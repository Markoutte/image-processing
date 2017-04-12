package me.markoutte.algorithm;

import me.markoutte.ds.Channel;
import me.markoutte.ds.Color;

public enum ColorHeuristics implements Heuristics {
    MEAN {
        @Override
        public int getWeight(int left, int right) {
            int redDiff = Math.abs(Color.getChannel(left, Channel.RED) - Color.getChannel(right, Channel.RED));
            int greenDiff = Math.abs(Color.getChannel(left, Channel.GREEN) - Color.getChannel(right, Channel.GREEN));
            int blueDiff = Math.abs(Color.getChannel(left, Channel.BLUE) - Color.getChannel(right, Channel.BLUE));

            return (int) Math.ceil((redDiff + greenDiff + blueDiff)/3.0);
        }
    },

    GRAYSCALE {
        @Override
        public int getWeight(int left, int right) {
            return Math.abs(Color.getGray(left) - Color.getGray(right));
        }
    },

    EUCLIDEAN {
        @Override
        public int getWeight(int left, int right) {
            double r = (Math.abs(Color.getChannel(left, Channel.RED) + Color.getChannel(right, Channel.RED))) / 2d;
            int redDiff = Math.abs(Color.getChannel(left, Channel.RED) - Color.getChannel(right, Channel.RED));
            int greenDiff = Math.abs(Color.getChannel(left, Channel.GREEN) - Color.getChannel(right, Channel.GREEN));
            int blueDiff = Math.abs(Color.getChannel(left, Channel.BLUE) - Color.getChannel(right, Channel.BLUE));

            return (int) Math.sqrt((2 + r / 256) * redDiff * redDiff + 4 * greenDiff * greenDiff + (2 + (255 - r) / 256) * blueDiff * blueDiff);
        }
    }
}
