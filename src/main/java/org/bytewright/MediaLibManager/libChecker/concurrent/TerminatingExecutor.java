package org.bytewright.MediaLibManager.libChecker.concurrent;

import org.bytewright.MediaLibManager.libChecker.ResultCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TerminatingExecutor extends ThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminatingExecutor.class);
    private static final int WORKER_COUNT = 10;
    private final ApplicationContext context;
    private final ResultCollector resultCollector;

    @Autowired
    public TerminatingExecutor(ApplicationContext context, ResultCollector resultCollector) {
        super(WORKER_COUNT, WORKER_COUNT, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.context = context;
        this.resultCollector = resultCollector;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        int activeCount = getActiveCount();
        int queueSize = getQueue().size();
        if (activeCount == 1 && queueSize == 0) {
            LOGGER.info("All tasks finished, init shutdown!");
            resultCollector.compileResult();
            shutdown();
            SpringApplication.exit(context, () -> 0);
        } else {
            LOGGER.debug("Task completed, current active threads: {}, queue size: {}", activeCount, queueSize);
        }
    }
}
