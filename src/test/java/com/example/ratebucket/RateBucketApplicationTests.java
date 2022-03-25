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

class RateBucketApplicationTests {

    @Test
    void should_invoid_long_over_flow_throw_exception_when_tokens_too_big() throws Exception {
        RateBucket rateBucket = RateBucket.Builder()
                .withLimitPerSecond(Long.MAX_VALUE)
                .build();

        Thread.sleep(Duration.ofSeconds(3).toMillis());

        assertThat(rateBucket.tryConsume(1)).isTrue();

    }

    @Nested
    class LocalRateucketTest {
        private LocalRateBucketBuilder bucket = RateBucket.Builder()
                .withLimitPerSecond(10);

        private double permittedRatePerSecond = 10;

        private void test5Seconds(Supplier<RateBucket> bucket, int threadCount, Function<RateBucket, Long> action) throws Throwable {
            ConsumptionScenario scenario = new ConsumptionScenario(threadCount, TimeUnit.SECONDS.toNanos(5), bucket, action, permittedRatePerSecond);
            scenario.executeAndValidateRate();
        }

        private double dsad = 1;

        @Test
        void should_try_consume_lock_free_with_one_thread() throws Throwable{
            int threadCount = 1; 
            Function<RateBucket, Long> action = b -> b.tryConsume(1)? 1L : 0L;
            test5Seconds(() -> bucket.build(), threadCount, action);
        }

        @Test
        void should_try_consume__lock_free_with_multi_thread() throws Throwable{
            int threadCount = 4; 
            Function<RateBucket, Long> action = b -> b.tryConsume(1)? 1L : 0L;
            test5Seconds(() -> bucket.build(), threadCount, action);
        }

        @Test
        void should_try_consume_with_lock() throws Throwable{
            int threadCount = 4;
            Function<RateBucket, Long> action = b -> b.tryConsume(1)? 1L : 0L;
            test5Seconds(() -> bucket.withSynchronize().build(), threadCount, action);
        }

        
    }
}
