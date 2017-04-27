package me.markoutte.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public final class MeasureUtils {

    public static long getObjectSize(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            return -1;
        }
        return baos.size();
    }

    public static Stopwatch createTimer() {
        return new Stopwatch();
    }

    public static class Stopwatch {
        private final long start;
        private long stop;

        public Stopwatch() {
            this.start = System.currentTimeMillis();
        }

        public void stop(StopwatchLog log) {
            this.stop = System.currentTimeMillis();
            log.log(stop - start);
        }
    }

    public interface StopwatchLog {
        void log(long totalTime);
    }

    private MeasureUtils() {
    }
}