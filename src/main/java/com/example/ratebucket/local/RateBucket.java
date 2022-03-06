package  com.example.ratebucket.local;

public interface RateBucket {
    static LocalRateBucketBuilder Builder() {
        return new LocalRateBucketBuilder();
    }
    boolean tryConsume(long tokens);
}
