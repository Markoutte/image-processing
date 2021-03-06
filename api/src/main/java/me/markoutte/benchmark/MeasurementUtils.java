package me.markoutte.benchmark;

import org.openjdk.jol.info.GraphLayout;

import java.util.function.Consumer;

public final class MeasurementUtils {

    public static Stopwatch startStopwatch() {
        return new Stopwatch();
    }

    public static boolean isDumpObjectSize() {
        return Boolean.getBoolean("memory.objects.dump");
    }

    public static void dumpObjectSize(Object instance, Consumer<Long> consumer) {
        if (Boolean.getBoolean("memory.objects.dump")) {
            if (consumer != null) {
                consumer.accept(GraphLayout.parseInstance(instance).totalSize());
                return;
            }
            System.out.println(GraphLayout.parseInstance(instance).toFootprint());
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