package yuki.android.template.repository.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import yuki.android.template.domain.executor.ThreadExecutor;

/**
 * Decorated {@link java.util.concurrent.ThreadPoolExecutor}
 */
public class JobExecutor implements ThreadExecutor {

    private static final int INITIAL_POOL_SIZE = 3;

    private static final int MAX_POOL_SIZE = 5;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ThreadFactory threadFactory;

    public JobExecutor() {
        this.workQueue = new LinkedBlockingQueue<>();
        this.threadFactory = new JobThreadFactory();
        this.threadPoolExecutor =
                new ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
                        KEEP_ALIVE_TIME,
                        KEEP_ALIVE_TIME_UNIT, this.workQueue,
                        this.threadFactory);
    }

    /**
     * {@inheritDoc}
     *
     * @param runnable The class that implements {@link Runnable} interface.
     */
    @Override
    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException(
                    "Runnable to execute cannot be null");
        }
        this.threadPoolExecutor.execute(runnable);
    }

    private static class JobThreadFactory implements ThreadFactory {

        private static final String THREAD_NAME_PREFIX = "android_";

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable,
                    THREAD_NAME_PREFIX + threadNumber.getAndIncrement());
        }
    }
}