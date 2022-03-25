package com.example.ratebucket.local;

import java.util.concurrent.locks.Lock;

import com.example.ratebucket.util.TimeMeter;

public class SynchronizedBucket extends LocalRateBucket {
    private final BandWith bandWith;

    private final TimeMeter timeMeter;

    private final Lock lock;

    public SynchronizedBucket(long limitPerSecond, TimeMeter timeMeter, Lock lock, long initialTokens) {
        super(limitPerSecond);
        this.lock = lock;
        this.bandWith = new BandWith(timeMeter.currentTimeNanos(), initialTokens);
        this.timeMeter = timeMeter;
    }

    @Override
    public boolean tryConsume(long tokens) {

        try {
            lock.lock();

            refillBandWith(bandWith, timeMeter.currentTimeNanos());

            long currentAvailableTokens = bandWith.getAvailableTokens();

            if (currentAvailableTokens < tokens) {
                return false;
            }

            long newSize = currentAvailableTokens - tokens;
            bandWith.setAvailableTokens(newSize);
        } finally {
            lock.unlock();
        }

        return true;

    }

    @Override
    public long getLastRefillNanos() {
        return bandWith.getLastRefillNanos();
    }

}
