package cn.octopusyan.alistgui.manager.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 *
 * @author octopus_yan@foxmail.com
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    private static final Logger logger = LoggerFactory.getLogger(ThreadFactory.class);

    public static final String DEFAULT_THREAD_PREFIX = "thread-factory-pool";

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ThreadFactory() {
        this(DEFAULT_THREAD_PREFIX);
    }

    public ThreadFactory(String prefix) {
        group = Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {

        Thread t = new Thread(group, runnable,
                namePrefix + threadNumber.getAndIncrement(),
                0);

        t.setUncaughtExceptionHandler((t1, e) -> {
            logger.error("thread : {}, error", t1.getName(), e);
        });

        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
