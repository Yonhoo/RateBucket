package com.example.ratebucket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import com.example.ratebucket.local.RateBucket;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			}
		}

	}

	@Test
	void should_invoid_long_over_flow_when_tokens_too_big() {
		RateBucket rateBucket = RateBucket.Builder()
				.withLimitPerSecond(Long.MAX_VALUE)
				.build();

		try {
			Thread.sleep(Duration.ofSeconds(3).toMillis());
		} catch (Exception e) {

		}

		assertThat(rateBucket.tryConsume(1)).isTrue();

	}

	@Test
	void should_rate_limit_when_try_consume_in_multi_thread() {
		RateBucket rateBucket = RateBucket.Builder()
				.withLimitPerSecond(1)
				.withThreadSafety()
				.build();

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					long lastConsumeTime = System.nanoTime();
					tryConsumeAndAssertTrue(rateBucket, lastConsumeTime);
				}

			}).start();
		}

	}

	private void tryConsumeAndAssertTrue(RateBucket rateBucket, long lastConsumeTime) {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(Duration.ofSeconds(1).toMillis());
			} catch (Exception e) {
			}

			if (rateBucket.tryConsume(1)) {
				long lastRefillNanos = rateBucket.getLastRefillNanos();
				assertThat(lastRefillNanos - lastConsumeTime).isGreaterThan(Duration.ofSeconds(1).toNanos());
				log.info("consume");
				lastConsumeTime = lastRefillNanos;
			}
		}
	}

}
