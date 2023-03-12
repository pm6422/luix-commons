package com.luixtech.utilities.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <pre>
 * The idea comes from codes of tomcat org.apache.catalina.core..StandardThreadExecutor
 *
 * {@link NetworkThreadPoolExecutor}
 * Execution strategy：运行线程直到maximumPoolSize后才将新任务加入workQueue中，如果workQueue满了就reject。
 * 当突然有个流量高峰的时候，能够快速的达到最大线程数，尽量把任务处理完，处理不完才入队列。
 * Applicable scenario：Scenarios where business processing requires remote resources
 *
 * {@link java.util.concurrent.ThreadPoolExecutor}
 * Execution strategy：运行线程大于corePoolSize时将新任务加入workQueue中，workQueue满后再扩充线程到maximumPoolSize，如果已经到了maximumPoolSize就reject。
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