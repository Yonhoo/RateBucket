package com.example.ratebucket.util;

import java.text.MessageFormat;

public class BucketExceptions {
    public static IllegalArgumentException nonPositiveCapacity(long initialTokens) {
        String pattern = "{0} is wrong value for capacity, because capacity should be positive";
        String msg = MessageFormat.format(pattern, initialTokens);
        return new IllegalArgumentException(msg);
    }

    public static IllegalArgumentException initialTokensOverLimit(long initialTokens) {
        String pattern = "{0} is wrong value for initial tokens, because limit less than initialTokens";
        String msg = MessageFormat.format(pattern, initialTokens);
        return new IllegalArgumentException(msg);
    }
}
