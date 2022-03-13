package com.example.TestUtil;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import com.example.ratebucket.local.RateBucket;

public class ConsumerThread extends Thread {

    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;
    private final RateBucket bucket;
    private final long workTimeNanos;
    private final Function<RateBucket, Long> action;
    private long consumed;
    private Throwable exception;

    public ConsumerThread(CountDownLatch startLatch, CountDownLatch endLatch, RateBucket bucket, long workTimeNanos,
            Function<RateBucket, Long> action) {
        this.startLatch = startLatch;
        this.endLatch = endLatch;
        this.bucket = bucket;
        this.workTimeNanos = workTimeNanos;
        this.action = action;
    }

    @Override
    public void run() {
        try {
            startLatch.countDown();
            startLatch.await(); // await for all consumer thread ready

            long endConsumeTime = System.nanoTime() + workTimeNanos; // execution time
            while (true) {
                if (endConsumeTime <= System.nanoTime()) {
                    return;
                }
                consumed += action.apply(bucket);
            }

        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
        } finally {
            endLatch.countDown();
        }
    }

    public long getConsumed() {
        return consumed;
    }

    public Throwable getException() {
        return exception;
    }

}
