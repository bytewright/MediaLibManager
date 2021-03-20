package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.libChecker.concurrent.TerminatingExecutor;
import org.bytewright.MediaLibManager.libChecker.concurrent.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryChecker implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryChecker.class);
    @Autowired
    private DirSource dirSource;
    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private TerminatingExecutor terminatingExecutor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Starting to parse library after ContextRefreshedEvent: {}", event);
        try {
            startChecking();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
