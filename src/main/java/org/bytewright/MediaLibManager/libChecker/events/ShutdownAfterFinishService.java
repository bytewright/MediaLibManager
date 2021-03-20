package org.bytewright.MediaLibManager.libChecker.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class ShutdownAfterFinishService implements ApplicationListener<LibraryCheckFinishedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownAfterFinishService.class);
    private final ApplicationContext context;

    @Autowired
    public ShutdownAfterFinishService(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(LibraryCheckFinishedEvent event) {
        LOGGER.info("Shutting down app after received LibraryCheckFinishedEvent");
        SpringApplication.exit(context, () -> 0);
    }
}
