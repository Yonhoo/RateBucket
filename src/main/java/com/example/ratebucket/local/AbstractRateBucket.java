package com.example.ratebucket.local;

import java.time.Duration;

import com.example.ratebucket.util.TimeMeter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRateBucket implements RateBucket {

    // TODO thread safety
    private long refillPeriodNanos;
    private long capacity;
    private long refillTokens;
    private BandWith bandWith;
    private TimeMeter timeMeter;

    public AbstractRateBucket(long limitPerSecond, TimeMeter timeMeter) {
        this.refillPeriodNanos = Duration.ofSeconds(1).toNanos();
        this.capacity = limitPerSecond;
        this.refillTokens = limitPerSecond;
        bandWith = new BandWith(timeMeter.currentTimeNanos(), limitPerSecond);
        this.timeMeter = timeMeter;
    }

    public long getCurrentTimeNanos() {
        return timeMeter.currentTimeNanos();
    }

    public long getLaseRefillNanos() {
        return bandWith.getLastRefillNanos();
    }

    public long getAvailableTokens() {
        return bandWith.getAvailableTokens();
    }

    public long getRoundingError() {
        return bandWith.getRoundingError();
    }

    public void setLastRefillNanos(long currentRefillNanos) {
        bandWith.setLastRefillNanos(currentRefillNanos);
    }

    public void setAvailableTokens(long availableTokens) {
        bandWith.setLastRefillNanos(availableTokens);
    }

    public void setRoundingError(long roundingError) {
        bandWith.setRoundingError(roundingError);
    }

}
