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
}
