package com.luixtech.utilities.thread.pool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 * The idea comes from codes of tomcat org.apache.catalina.core..StandardThreadExecutor
 *
 * {@link NetworkThreadPoolExecutor}
 * Execution strategy: Keep creating threads until reaching maximumPoolSize, then enqueue new tasks into workQueue.
 * If workQueue is full, reject the task.
 * This helps quickly ramp up to the maximum thread count during a sudden traffic spike and process tasks first;
 * only tasks that cannot be processed immediately will be queued.
 * Applicable scenario：Scenarios where business processing requires remote resources
 *
 * {@link java.util.concurrent.ThreadPoolExecutor}
 * Execution strategy: When the pool size is greater than corePoolSize, enqueue new tasks into workQueue first.
 * Only when workQueue is full will it expand the pool up to maximumPoolSize; if it is already at maximumPoolSize,
 * reject the task.
 * Applicable scenario：CPU intensive applications (e.g. All operations performed inside runnable are In-JVM, memory copy, or compute. etc.)
 * </pre>
 */
public class NetworkThreadPoolExecutor extends ThreadPoolExecutor {
    public static final int           DEFAULT_CORE_POOL_SIZE  = 20;
    public static final int           DEFAULT_MAX_POOL_SIZE   = 200;
    /**
     * 1 minute
     */
    public static final int           DEFAULT_KEEP_ALIVE_TIME = 60 * 1000;
    /**
     * Processing task count
     */
    protected           AtomicInteger submittedTasksCount     = new AtomicInteger(0);
    /**
     * Maximum processing task count limit: queueCapacity + maximumPoolSize
     */
    private final       int           maxSubmittedTasksCount;

    public NetworkThreadPoolExecutor() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, maximumPoolSize);
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, maximumPoolSize);
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int queueCapacity) {
        this(corePoolSize, maximumPoolSize, queueCapacity, Executors.defaultThreadFactory());
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int queueCapacity, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queueCapacity, threadFactory);
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueCapacity) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity, Executors.defaultThreadFactory());
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     int queueCapacity, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, queueCapacity, threadFactory, new AbortPolicy());
    }

    public NetworkThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ExecutorQueue(), threadFactory, handler);
        ((ExecutorQueue) getQueue()).setStandardThreadExecutor(this);

        // Maximum processing task count limit: queueCapacity + maximumPoolSize
        maxSubmittedTasksCount = queueCapacity + maximumPoolSize;
    }

    @Override
    public void execute(Runnable command) {
        int count = submittedTasksCount.incrementAndGet();

        // LinkedTransferQueue has no capacity limit, so we need to execute reject policy when exceeding the maxSubmittedTasksCount
        if (count > maxSubmittedTasksCount) {
            submittedTasksCount.decrementAndGet();
            getRejectedExecutionHandler().rejectedExecution(command, this);
        }

        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            if (!((ExecutorQueue) getQueue()).force(command)) {
                submittedTasksCount.decrementAndGet();
                getRejectedExecutionHandler().rejectedExecution(command, this);
            }
        }
    }

    public int getSubmittedTasksCount() {
        return this.submittedTasksCount.get();
    }

    public int getMaxSubmittedTasksCount() {
        return maxSubmittedTasksCount;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedTasksCount.decrementAndGet();
    }
}

/**
 * <pre>
 * 1. LinkedTransferQueue has a better performance comparing with LinkedBlockingQueue
 * 2. But LinkedTransferQueue has no capacity limit control, so we need to implement the logic outside the queue
 * </pre>
 */
class ExecutorQueue extends LinkedTransferQueue<Runnable> {
    private static final long                      serialVersionUID = 1693153562045930859L;
    private              NetworkThreadPoolExecutor threadPoolExecutor;

    public ExecutorQueue() {
        super();
    }

    public void setStandardThreadExecutor(NetworkThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public boolean force(Runnable o) {
        if (threadPoolExecutor.isShutdown()) {
            throw new RejectedExecutionException("Can NOT insert a task into the queue after the executor shutdown!");
        }
        // Inserts the specified element at the tail of this queue.
        return super.offer(o);
    }

    @Override
    public boolean offer(Runnable o) {
        int poolSize = threadPoolExecutor.getPoolSize();

        // we are maxed out on threads, simply queue the object
        if (poolSize == threadPoolExecutor.getMaximumPoolSize()) {
            return super.offer(o);
        }
        // we have idle threads, just add it to the queue
        // note that we don't use getActiveCount(), see BZ 49730
        if (threadPoolExecutor.getSubmittedTasksCount() <= poolSize) {
            return super.offer(o);
        }
        // if we have less threads than maximum force creation of a new thread
        if (poolSize < threadPoolExecutor.getMaximumPoolSize()) {
            return false;
        }
        // if we reached here, we need to add it to the queue
        return super.offer(o);
    }
}
