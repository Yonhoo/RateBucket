package com.example.ratebucket.local;

public class BandWith {
    private static final int BANDWIDTH_SIZE = 3;
    private volatile long[] bucketData;

    public BandWith(long lastRefillNanos, long availableTokens) {
        bucketData = new long[3];
        bucketData[0] = lastRefillNanos;
        bucketData[1] = availableTokens;
        bucketData[2] = 0;

    }

    public long getLastRefillNanos() {
        return bucketData[0];
    }

    public long getAvailableTokens() {
        return bucketData[1];
    }

    public long getRoundingError() {
        return bucketData[2];
    }

    public void setLastRefillNanos(long lastRefillNanos) {
        bucketData[0] = lastRefillNanos;
    }

    public void setAvailableTokens(long availableTokens) {
        bucketData[1] = availableTokens;
    }

    public void setRoundingError(Long roundingError) {
        bucketData[2] = roundingError;
    }
}
