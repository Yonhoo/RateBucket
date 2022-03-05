package com.example.ratebucket.local;


public abstract class AbstractRateBucket implements RateBucket {

    // TODO thread safety
    long limitPerSecond;
    long availableTokens;

    public AbstractRateBucket() {
        this.limitPerSecond = 0;
        this.availableTokens = 0;
    }

    @Override
    public long getAvailableTokens() {
        return availableTokens;
    }

    @Override
    public RateBucket getRateBucket(int limitPerSecond) {
        return buildRateBucket(limitPerSecond);
    }

    public abstract RateBucket buildRateBucket(int limitPerSecond);

}
