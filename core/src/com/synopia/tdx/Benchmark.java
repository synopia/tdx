package com.synopia.tdx;

import java.util.concurrent.Callable;

/**
 * Created by synopia on 09.01.2015.
 */
public abstract class Benchmark {
    public static long lastTimeMs;

    public static <T> T bench(Callable<T> callable) {
        try {
            long start = System.nanoTime();
            T result = callable.call();
            long end = System.nanoTime();
            lastTimeMs = (end-start)/1000;
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
