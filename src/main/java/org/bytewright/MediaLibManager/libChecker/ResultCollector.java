package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.resultDTOs.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class ResultCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultCollector.class);
    private final Queue<CheckResult> checkResults = new ConcurrentLinkedDeque<>();
    private List<Thread> tasks;
    @Autowired
    private ResultTypesProvider resultTypesProvider;

    public void startAndAwaitTasks() {
        Instant startInstant = Instant.now();
        for (Thread task : tasks) {
            task.start();
        }
        LOGGER.info("All {} tasks have been started", tasks.size());
        for (Thread task : tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Finished checking all elements, duration: {}ms", Duration.between(startInstant, Instant.now()).toMillis());
    }

    public void compileResult() {
        LOGGER.info("Got {} results from the checking, compiling...", checkResults.size());
        for (Class<? extends CheckResult> resultType : resultTypesProvider.getResultTypes()) {
            List<? extends CheckResult> resultsOfType = this.checkResults.stream()
                    .filter(checkResult -> resultType.isAssignableFrom(checkResult.getClass()))
                    .map(resultType::cast)
                    .collect(Collectors.toList());
            resultsOfType = resultTypesProvider.sort(resultType, resultsOfType);
            if (resultsOfType.size() > 0) {
                LOGGER.warn("Found {} issues of type {}:", resultsOfType.size(), resultType);
                resultsOfType.forEach(result -> LOGGER.warn("{}", result.getResultLogLine()));
            }
        }
    }

    public void setTasks(List<LibElement> tasks) {
        this.tasks = tasks.stream()
                .peek(libElement -> libElement.registerCollector(checkResults::add))
                .map(Thread::new)
                .collect(Collectors.toList());
    }
}
