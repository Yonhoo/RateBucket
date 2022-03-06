package  com.example.ratebucket.local;

public interface RateBucket {
    LocalRateBucketBuilder Builder(int limitPerSecond);
    long getAvailableTokens();
    boolean tryConsume(int tokens);
    void tryConsumeWithBlock();
}
