package com.luixtech.springbootframework.async;

import com.luixtech.springbootframework.utils.TraceIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Async task executor with exception handling.
 */
@Slf4j
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

    private final AsyncTaskExecutor executor;

    public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(createWrappedRunnable(task, MDC.getCopyOfContextMap()));
    }

    @Deprecated
    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.execute(createWrappedRunnable(task, MDC.getCopyOfContextMap()), startTimeout);
    }

    private <T> Callable<T> createCallable(final Callable<T> task, final Map<String, String> context) {
        return () -> {
            try {
                TraceIdUtils.setParentMdcToChild(context);
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            } finally {
                TraceIdUtils.remove();
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task, final Map<String, String> context) {
        return () -> {
            try {
                TraceIdUtils.setParentMdcToChild(context);
                task.run();
            } catch (Exception e) {
                handle(e);
            } finally {
                TraceIdUtils.remove();
            }
        };
    }

    protected void handle(Exception e) {
        log.error("Caught async task exception", e);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(createWrappedRunnable(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(createCallable(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean) executor;
            bean.afterPropertiesSet();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executor instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean) executor;
            bean.destroy();
        }
    }
}
