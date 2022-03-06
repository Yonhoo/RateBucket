package com.example.ratebucket.local;

import com.example.ratebucket.util.BucketExceptions;
import com.example.ratebucket.util.TimeMeter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LocalRateBucketBuilder {

    private long limit;

    private TimeMeter timeMeter = TimeMeter.SYSTEM_NANOTIME;
    // private SynchronizationStrategy synchronizationStrategy =
    // SynchronizationStrategy.LOCK_FREE;

    public LocalRateBucketBuilder withLimitPerSecond(long limit) {
        if (limit <= 0) {
            throw BucketExceptions.nonPositiveCapacity(limit);
        }
        this.limit = limit;
        return this;
    }

    public RateBucket build() {
        return new LocalRateBucket(limit, timeMeter);
    }
}
