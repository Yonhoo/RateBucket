package com.example.ratebucket.local;

import com.example.util.TimeMeter;

public class LocalRateBucket extends AbstractRateBucket {

    @Override
    public RateBucket buildRateBucket(int limitPerSecond) {
        return null;
    }

    @Override
    public TimeMeter getTimeMeter() {
        return TimeMeter.SYSTEM_NANOTIME;
    }
}
