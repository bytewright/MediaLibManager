package org.bytewright.MediaLibManager.libChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryChecker.class);

    @Autowired
    private DirSource dirSource;
    @Autowired
    private ResultCollector resultCollector;

    @EventListener
    @Async
    public void onContextRefreshed(ContextRefreshedEvent event) {
        LOGGER.info("Starting to parse library after ContextRefreshedEvent: {}", event);
        for (Path directory : dirSource.getDirectories()) {
            List<File> fileList = Arrays.stream(directory.toFile().listFiles())
                    //.limit(20)
                    .collect(Collectors.toList());
            LOGGER.info("Found {} elements in given source dir {}, starting to check them", fileList.size(), directory);
            List<LibElement> tasks = new LinkedList<>();
            for (File file : fileList) {
                LibElement libElement = new LibElement(file);
                tasks.add(libElement);
            }
            resultCollector.setTasks(tasks);
            Instant startInstant = Instant.now();
            resultCollector.startAndAwaitTasks();
            LOGGER.info("Finished checking all elements in {}, duration: {}ms", directory, Duration.between(startInstant, Instant.now()).toMillis());
            resultCollector.compileResult();
        }
    }
}
