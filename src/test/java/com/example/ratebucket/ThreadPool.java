package com.example.ratebucket;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    ThreadPoolExecutor threadPool;

    public ThreadPool() {
        threadPool = new ThreadPoolExecutor(5, 6, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void execute(Runnable r) {
            threadPool.execute(r);
    }
}
