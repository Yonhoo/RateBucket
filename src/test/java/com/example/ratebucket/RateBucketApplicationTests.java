package com.example.ratebucket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.TestUtil.ConsumptionScenario;
import com.example.ratebucket.local.LocalRateBucketBuilder;
import com.example.ratebucket.local.RateBucket;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class RateBucketApplicationTests {

    @Test
    void should_rate_limit_1_per_second_with_thread_not_safety() throws Exception {
        RateBucket rateBucket = RateBucket.Builder()
                .withLimitPerSecond(1)
                .build();
        long lastConsumeTime = System.nanoTime() - Duration.ofSeconds(1).toNanos();
        long consumeNums = 0;
        for (int i = 0; i < 10; i++) {

            if (rateBucket.tryConsume(1)) {
                consumeNums++;
                long lastRefillNanos = rateBucket.getLastRefillNanos();
                assertThat(lastRefillNanos - lastConsumeTime).isGreaterThan(Duration.ofSeconds(1).toNanos());
                lastConsumeTime = lastRefillNanos;
            } else {
                assertThat(consumeNums).isEqualTo(1);
                consumeNums = 0;
                Thread.sleep(Duration.ofSeconds(1).toMillis());
            }
        }

    }

    @Test
    void should_rate_limit_5_per_second_with_thread_not_safety() throws Exception {
        RateBucket rateBucket = RateBucket.Builder()
                .withLimitPerSecond(5)
                .build();
        long lastRejectConsumeTime = System.nanoTime() - Duration.ofSeconds(1).toNanos();
        long consumeNums = 0;
        for (int i = 0; i < 10; i++) {
            if (rateBucket.tryConsume(1)) {
                consumeNums++;
                assertThat(System.nanoTime() - lastRejectConsumeTime).isGreaterThan(Duration.ofSeconds(1).toNanos());
            } else {
                lastRejectConsumeTime = System.nanoTime();
                assertThat(consumeNums).isEqualTo(5);
                consumeNums = 0;
                Thread.sleep(Duration.ofSeconds(2).toMillis());
            }
        }
    }

    @Test
    void should_invoid_long_over_flow_throw_exception_when_tokens_too_big() throws Exception {
        RateBucket rateBucket = RateBucket.Builder()
                .withLimitPerSecond(Long.MAX_VALUE)
                .build();

        Thread.sleep(Duration.ofSeconds(3).toMillis());

        assertThat(rateBucket.tryConsume(1)).isTrue();

    }

    @Test
    void should_rate_limit_when_try_consume_in_multi_thread() {
        RateBucket rateBucket = RateBucket.Builder()
                .withLimitPerSecond(1)
                .withThreadSafety()
                .build();

        var pool = new ThreadPool();

        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                long lastConsumeTime = System.nanoTime() - Duration.ofSeconds(1).toNanos();
                assertThat(tryConsume(rateBucket, lastConsumeTime)).isTrue();
            });
        }

    }

    private boolean tryConsume(RateBucket rateBucket, long lastConsumeTime) {
        long consumeNums = 0;
        for (int i = 0; i < 10; i++) {
            if (rateBucket.tryConsume(1)) {
                consumeNums++;
                long lastRefillNanos = rateBucket.getLastRefillNanos();
                assertThat(lastRefillNanos - lastConsumeTime).isGreaterThan(Duration.ofSeconds(1).toNanos());
                lastConsumeTime = lastRefillNanos;
            } else {
                try {
                    assertThat(consumeNums).isEqualTo(1);
                    consumeNums = 0;
                    Thread.sleep(Duration.ofSeconds(1).toMillis());
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return true;
    }

    @Nested
    class LocalRateucketTest {
        private LocalRateBucketBuilder bucket = RateBucket.Builder()
                .withLimitPerSecond(10);

        private double permittedRatePerSecond = 10;
        private double test = 100;
        private double ds = 231;

        private void test5Seconds(Supplier<RateBucket> bucket, int threadCount, Function<RateBucket, Long> action) throws Throwable {
            ConsumptionScenario scenario = new ConsumptionScenario(threadCount, TimeUnit.SECONDS.toNanos(5), bucket, action, permittedRatePerSecond);
            scenario.executeAndValidateRate();
        }

        @Test
        void should_try_consume_unsafety() throws Throwable{
            int threadCount = 1; // multi unsafety
            Function<RateBucket, Long> action = b -> b.tryConsume(1)? 1L : 0L;
            test5Seconds(() -> bucket.build(), threadCount, action);
        }

        @Test
        void should_try_consume_with_lock() throws Throwable{
            int threadCount = 4;
            Function<RateBucket, Long> action = b -> b.tryConsume(1)? 1L : 0L;
            test5Seconds(() -> bucket.withThreadSafety().build(), threadCount, action);
        }

        
    }
}
