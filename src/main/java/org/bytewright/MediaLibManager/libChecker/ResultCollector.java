package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.resultDTOs.CheckNameResult;
import org.bytewright.MediaLibManager.resultDTOs.CheckResult;
import org.bytewright.MediaLibManager.resultDTOs.CheckVideoCountResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class ResultCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultCollector.class);
    private List<Thread> tasks;
    private final Queue<CheckResult> checkResults = new ConcurrentLinkedDeque<>();
    @Autowired
    private ResultTypesProvider resultTypesProvider;

    public void startAndAwaitTasks() {
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

    }

    public void compileResult() {
        LOGGER.info("Got {} results from the checking, compiling...", checkResults.size());
        for (Class<? extends CheckResult> resultType : resultTypesProvider.getResultTypes()) {
            List<? extends CheckResult> checkResults = this.checkResults.stream()
                    .filter(checkResult -> resultType.isAssignableFrom(checkResult.getClass()))
                    .map(resultType::cast)
                    .collect(Collectors.toList());
            checkResults = resultTypesProvider.sort(resultType, checkResults);
            if (checkResults.size() > 0) {
                LOGGER.warn("Found {} issues of type {}:", checkResults.size(), resultType);
                checkResults.forEach(result -> LOGGER.warn("{}", result.getResultLogLine()));
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
