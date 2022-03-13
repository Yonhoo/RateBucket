package com.example.TestUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.ratebucket.local.RateBucket;

public class ConsumptionScenario {

    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;
    private final ConsumerThread[] consumers;
    private final long initializationTimeMillis;
    private final double permittedRatePerSecond;

    public ConsumptionScenario(int threadCount, long workTimeNanos, Supplier<RateBucket> bucketSupplier,
            Function<RateBucket, Long> action, double permittedRatePerSecond) {
        this.startLatch = new CountDownLatch(threadCount);
        this.endLatch = new CountDownLatch(threadCount);
        this.consumers = new ConsumerThread[threadCount];
        this.initializationTimeMillis = System.currentTimeMillis();
        this.permittedRatePerSecond = permittedRatePerSecond;
        RateBucket bucket = bucketSupplier.get();
        for (int i = 0; i < threadCount; i++) {
            this.consumers[i] = new ConsumerThread(startLatch, endLatch, bucket, workTimeNanos, action);
        }
    }

    public void executeAndValidateRate() throws Throwable {

        for (ConsumerThread consumerThread : consumers) {
            consumerThread.start();
        }
        endLatch.await();
        long durationMillis = System.currentTimeMillis() - initializationTimeMillis;

        long totalConsumeTokens = 0;
        for (ConsumerThread consumerThread : consumers) {
            var exception = consumerThread.getException();
            if (exception != null) {
                throw exception;
            }
            totalConsumeTokens += consumerThread.getConsumed();
        }

        double actualRatePerSecond = (double) totalConsumeTokens * 1_000.0d / durationMillis;
        System.out.println("Consumed " + totalConsumeTokens + " tokens in the "
                + durationMillis + " millis, actualRatePerSecond=" + actualRatePerSecond
                + ", permitted rate=" + permittedRatePerSecond);

        String msg = "Actual rate " + actualRatePerSecond + " is greater then permitted rate " + permittedRatePerSecond;
        assertTrue(actualRatePerSecond <= permittedRatePerSecond, msg);

    }

}
