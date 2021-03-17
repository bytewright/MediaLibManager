package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.libChecker.concurrent.TerminatingExecutor;
import org.bytewright.MediaLibManager.libChecker.concurrent.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryChecker.class);
    @Autowired
    private DirSource dirSource;
    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private TerminatingExecutor terminatingExecutor;

    @EventListener
    @Async
    public void onContextRefreshed(ContextRefreshedEvent event) throws IOException {
        LOGGER.info("Starting to parse library after ContextRefreshedEvent: {}", event);
        startChecking();

    }

    private void startChecking() throws IOException {
        for (Path directory : dirSource.getDirectories()) {
            List<LibElement> elementList = Files.list(directory)
                    .map(LibElement::new)
                    .collect(Collectors.toList());
            LOGGER.info("Found {} elements in given source dir {}, starting to check them", elementList.size(), directory);
            workerFactory.setNewTaskConsumer(terminatingExecutor::submit);
            workerFactory.submitAll(elementList);
        }
    }
}
