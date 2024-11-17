package cn.octopusyan.alistgui.manager.thread;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类
 */
public final class ThreadPoolManager extends ThreadPoolExecutor {

    private static volatile ThreadPoolManager sInstance;

    private ThreadPoolManager() {
        super(32,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new ThreadFactory(ThreadFactory.DEFAULT_THREAD_PREFIX),
                new DiscardPolicy());
    }

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) sInstance = new ThreadPoolManager();
        return sInstance;
    }
}