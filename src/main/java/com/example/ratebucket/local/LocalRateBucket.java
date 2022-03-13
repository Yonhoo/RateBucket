package com.example.ratebucket.local;

import com.example.ratebucket.util.BucketExceptions;
import com.example.ratebucket.util.TimeMeter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalRateBucket extends AbstractRateBucket {

    public LocalRateBucket(long limitPerSecond, TimeMeter timeMeter) {
        super(limitPerSecond, timeMeter);
    }

    @Override
    public boolean tryConsume(long tokens) {

        if (tokens <= 0) {
            throw BucketExceptions.nonPositiveCapacity(tokens);
        }

        // compute available tokens
        long lastTimeRefillNanos = getLastRefillNanos();
        long currentAvailableTokens = getAvailableTokens();
        long currentTimeNanos = getCurrentTimeNanos();
        long refillPeriodNanos = getRefillPeriodNanos();
        long refillTokens = getRefillTokens();
        long newAvailableTokens = currentAvailableTokens;
        long durationSinceLastRefillNanos = currentTimeNanos - lastTimeRefillNanos;

        // duration < period and tokens not enough: return false
        if (currentAvailableTokens < tokens && durationSinceLastRefillNanos < refillPeriodNanos) {
            return false;
        }

        long periods = durationSinceLastRefillNanos / refillPeriodNanos;

        if (periods > 0) {
            newAvailableTokens = addExact(multipleExact(periods, refillTokens), newAvailableTokens);
        }
        durationSinceLastRefillNanos %= refillPeriodNanos;

        long roundingError = getRoundingError();
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

        long newSize = newAvailableTokens - tokens;
        if (newSize < 0) {
            return false;
        }

        if (getCapacity() <= newSize) {
            resetBandWith();
        }

        setAvailableTokens(newSize);
        setRoundingError(roundingError);
        setLastRefillNanos(currentTimeNanos);
        return true;
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

    private void resetBandWith() {
        this.setAvailableTokens(getCapacity());
        this.setRoundingError(0);
    }
}
