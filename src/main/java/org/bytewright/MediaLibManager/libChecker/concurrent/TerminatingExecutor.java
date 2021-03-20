package org.bytewright.MediaLibManager.libChecker.concurrent;

import org.bytewright.MediaLibManager.libChecker.ResultCollector;
import org.bytewright.MediaLibManager.libChecker.events.LibraryCheckFinishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TerminatingExecutor extends ThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminatingExecutor.class);
    private static final int WORKER_COUNT = 10;
    private final ResultCollector resultCollector;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TerminatingExecutor(ResultCollector resultCollector, ApplicationEventPublisher eventPublisher) {
        super(WORKER_COUNT, WORKER_COUNT, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.resultCollector = resultCollector;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        int activeCount = getActiveCount();
        int queueSize = getQueue().size();
        if (activeCount == 1 && queueSize == 0) {
            LOGGER.info("All tasks finished, init shutdown!");
            resultCollector.compileResult();
            shutdown();
            eventPublisher.publishEvent(new LibraryCheckFinishedEvent(this));
        } else {
            LOGGER.debug("Task completed, current active threads: {}, queue size: {}", activeCount, queueSize);
        }
    }
}
