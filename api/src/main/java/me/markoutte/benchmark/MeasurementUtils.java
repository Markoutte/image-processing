package me.markoutte.benchmark;

import org.apache.log4j.Logger;
import org.openjdk.jol.info.GraphLayout;

import java.util.function.Consumer;

public final class MeasurementUtils {

    private static final Logger L = Logger.getLogger(MeasurementUtils.class);

    public static Stopwatch startStopwatch() {
        return new Stopwatch();
    }

    public static void dumpObjectSize(Object instance) {
        if (Boolean.getBoolean("memory.objects.dump")) {
            L.info(GraphLayout.parseInstance(instance).toFootprint());
        }
    }

    public static class Stopwatch {
        private final long start;
        private long stop;

        public Stopwatch() {
            this.start = System.currentTimeMillis();
        }

        public void stop(Consumer<Long> log) {
            this.stop = System.currentTimeMillis();
            log.accept(stop - start);
        }
    }

    private MeasurementUtils() {
    }
}