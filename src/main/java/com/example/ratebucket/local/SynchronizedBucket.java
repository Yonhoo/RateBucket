package com.example.ratebucket.local;

import java.util.concurrent.locks.Lock;

import com.example.ratebucket.util.TimeMeter;

public class SynchronizedBucket extends LocalRateBucket {

    private final Lock lock;

    public SynchronizedBucket(long limitPerSecond, TimeMeter timeMeter, Lock lock) {
        super(limitPerSecond, timeMeter);
        this.lock = lock;
    }

    @Override
    public boolean tryConsume(long tokens) {

        try {
            lock.lock();
            return super.tryConsume(tokens);
        } finally {
            lock.unlock();
        }

    }

}
