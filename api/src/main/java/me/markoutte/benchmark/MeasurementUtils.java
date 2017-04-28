package me.markoutte.benchmark;

import me.markoutte.image.impl.ArraySegments;
import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MeasurementUtils {

    public static Stopwatch startStopwatch() {
        return new Stopwatch();
    }

    public static void dumpObjectSize(Object instance) {
        if (Boolean.getBoolean("memory.objects.dump")) {
            System.out.println(GraphLayout.parseInstance(instance).toFootprint());
        }
    }

    public static class Stopwatch {
        private final long start;
        private long stop;

        public Stopwatch() {
            this.start = System.currentTimeMillis();
        }

        public void stop(Function<Long, String> log) {
            this.stop = System.currentTimeMillis();
            System.out.println(log.apply(stop - start));
        }
    }

    private MeasurementUtils() {
    }
}