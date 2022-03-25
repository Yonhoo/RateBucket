package com.example.ratebucket.local;

import java.util.concurrent.atomic.AtomicReference;

import com.example.ratebucket.util.BucketExceptions;
import com.example.ratebucket.util.TimeMeter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalFreeBucket extends LocalRateBucket {
    private final AtomicReference<BandWith> atomicBandWith;

    private final TimeMeter timeMeter;

    public LocalFreeBucket(long limitPerSecond, TimeMeter timeMeter, long initialTokens) {
        super(limitPerSecond);
        this.atomicBandWith = new AtomicReference<>(new BandWith(timeMeter.currentTimeNanos(), initialTokens));
        this.timeMeter = timeMeter;
    }

    @Override
    public boolean tryConsume(long tokens) {
        long currentTimeNanos = timeMeter.currentTimeNanos();
        if (tokens <= 0) {
            throw BucketExceptions.nonPositiveCapacity(tokens);
        }

        while (true) {
            BandWith prevBandWith = atomicBandWith.get();
            BandWith newBandWith = prevBandWith.copy();

            refillBandWith(newBandWith, currentTimeNanos);

            long currentAvailableTokens = newBandWith.getAvailableTokens();

            if (currentAvailableTokens < tokens) {
                return false;
            }
            
            newBandWith.setAvailableTokens(currentAvailableTokens - tokens);
            if (atomicBandWith.compareAndSet(prevBandWith, newBandWith)) {
                return true;
            }
        }
    }

    @Override
    public long getLastRefillNanos() {
        return atomicBandWith.get().getLastRefillNanos();
    }
}
