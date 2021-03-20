package org.bytewright.MediaLibManager.libChecker.concurrent;

import org.bytewright.MediaLibManager.libChecker.LibElement;
import org.bytewright.MediaLibManager.libChecker.ResultCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class WorkerFactory {
    private TaskConsumer taskConsumer;
    private ResultCollector resultCollector;

    public void setNewTaskConsumer(TaskConsumer taskConsumer) {
        this.taskConsumer = taskConsumer;
    }

    @Autowired
    public void setResultCollector(ResultCollector resultCollector) {
        this.resultCollector = resultCollector;
    }

    public void submitTask(LibElement libElement) {
        LibElementCheckWorker worker = new LibElementCheckWorker(libElement, this::submitTask, resultCollector.getResultConsumer());
        taskConsumer.accept(worker);
    }

    public void submitAll(Collection<LibElement> taskQueue) {
        taskQueue.stream()
                .map(libElement -> new LibElementCheckWorker(libElement, this::submitTask, resultCollector.getResultConsumer()))
                .forEach(taskConsumer);
    }
}
