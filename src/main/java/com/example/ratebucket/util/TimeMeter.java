package com.example.ratebucket.util;

public interface TimeMeter {
    long currentTimeNanos();

    TimeMeter SYSTEM_NANOTIME = new TimeMeter() {

        @Override
        public long currentTimeNanos() {
            return System.nanoTime();
        }

    };
}
