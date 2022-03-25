package com.example.ratebucket.local;

import java.util.concurrent.locks.ReentrantLock;

import com.example.ratebucket.util.BucketExceptions;
import com.example.ratebucket.util.TimeMeter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LocalRateBucketBuilder {

    private long limit;

    private TimeMeter timeMeter = TimeMeter.SYSTEM_NANOTIME;

    private boolean threadSafety;

    private long initialTokens;

    public LocalRateBucketBuilder withLimitPerSecond(long limit) {
        if (limit <= 0) {
            throw BucketExceptions.nonPositiveCapacity(limit);
        }
        this.limit = limit;
        return this;
    }

    public LocalRateBucketBuilder withSynchronize() {
        this.threadSafety = true;
        return this;
    }

    public LocalRateBucketBuilder withInitialTokens(long initialTokens) {
        this.initialTokens = initialTokens;
        return this;
    }

    public RateBucket build() {
        if (limit < initialTokens) {
            BucketExceptions.initialTokensOverLimit(initialTokens);
        }
        if (threadSafety) {
            return new SynchronizedBucket(limit, timeMeter, new ReentrantLock(), initialTokens);
        }
        return new LocalFreeBucket(limit, timeMeter, initialTokens);
    }
}
