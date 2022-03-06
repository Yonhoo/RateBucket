package com.example.ratebucket.local;

import com.example.ratebucket.util.TimeMeter;

public class LocalRateBucketBuilder {

    private int limit;

    private TimeMeter timeMeter = TimeMeter.SYSTEM_NANOTIME;
    // private SynchronizationStrategy synchronizationStrategy =
    // SynchronizationStrategy.LOCK_FREE;

    public LocalRateBucketBuilder limitPerSecond(int limit) {
        this.limit = limit;
        return this;
    }

    public RateBucket build() {
        return new LocalRateBucket(limit,timeMeter);
    }
}
