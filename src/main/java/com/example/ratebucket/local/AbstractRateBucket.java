package com.example.ratebucket.local;

public abstract class AbstractRateBucket implements RateBucket {

    public abstract void refillBandWith(BandWith bandWith, long currentTimeNanos);

}
