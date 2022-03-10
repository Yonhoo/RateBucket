package com.example.ratebucket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import com.example.ratebucket.local.RateBucket;

import org.junit.jupiter.api.Test;

class RateBucketApplicationTests {

	@Test
	void should_rate_limit_with_thread_not_safety() {
		RateBucket rateBucket = RateBucket.Builder()
				.withLimitPerSecond(1)
				.build();
		long lastConsumeTime = System.nanoTime();

		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(Duration.ofSeconds(1).toMillis());
			} catch (Exception e) {
			}

			if (rateBucket.tryConsume(1)) {
				long lastRefillNanos = rateBucket.getLastRefillNanos();
				assertThat(lastRefillNanos - lastConsumeTime).isGreaterThan(Duration.ofSeconds(1).toNanos());
				lastConsumeTime = lastRefillNanos;
				System.out.println("lastRefillNanos: " + LocalDateTime.now());
			}
		}

	}

}
