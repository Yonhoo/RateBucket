package com.example.ratebucket.local;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LocalRateBucket extends AbstractRateBucket {

    private long refillPeriodNanos;
    private long capacity;
    private long refillTokens;

    public LocalRateBucket(long limitPerSecond) {
        this.refillPeriodNanos = Duration.ofSeconds(1).toNanos();
        this.capacity = limitPerSecond;
        this.refillTokens = limitPerSecond;
    }

    public void refillBandWith(BandWith bandWith, long currentTimeNanos) {

        // compute available tokens
        long lastTimeRefillNanos = bandWith.getLastRefillNanos();
        long currentAvailableTokens = bandWith.getAvailableTokens();
        long newAvailableTokens = currentAvailableTokens;
        long durationSinceLastRefillNanos = currentTimeNanos - lastTimeRefillNanos;

        if (durationSinceLastRefillNanos < refillPeriodNanos) {
            return;
        }

        long periods = durationSinceLastRefillNanos / refillPeriodNanos;

        if (periods > 0) {
            newAvailableTokens = addExact(multipleExact(periods, refillTokens), newAvailableTokens);
        }
        durationSinceLastRefillNanos %= refillPeriodNanos;

        long roundingError = bandWith.getRoundingError();
        long divided = multipleExact(durationSinceLastRefillNanos, refillTokens);
        divided = addExact(divided, roundingError);
        if (divided == Long.MAX_VALUE) {
            long calculatedRefill = (long) ((double) durationSinceLastRefillNanos / refillPeriodNanos * refillTokens);
            newAvailableTokens = addExact(newAvailableTokens, calculatedRefill);
            roundingError = 0;
        } else {
            long calculatedRefill = divided / refillPeriodNanos;
            if (calculatedRefill == 0) {
                roundingError = divided;
            } else {
                newAvailableTokens = addExact(newAvailableTokens, calculatedRefill);
                roundingError = divided % refillPeriodNanos;
            }
        }

        if (this.capacity <= newAvailableTokens) {
            newAvailableTokens = this.capacity;
            roundingError = 0;
        }

        bandWith.setAvailableTokens(newAvailableTokens);
        bandWith.setRoundingError(roundingError);
        bandWith.setLastRefillNanos(currentTimeNanos);
    }

    private long addExact(long a, long b) {
        try {
            return Math.addExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    private long multipleExact(long a, long b) {
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    public void resetBandWith(BandWith bandWith) {
        bandWith.setAvailableTokens(this.capacity);
        bandWith.setRoundingError(0L);
    }

    public long getCapacity() {
        return capacity;
    }

    
}
