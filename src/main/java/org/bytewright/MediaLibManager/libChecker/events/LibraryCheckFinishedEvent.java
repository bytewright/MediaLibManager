package org.bytewright.MediaLibManager.libChecker.events;

import org.springframework.context.ApplicationEvent;

public class LibraryCheckFinishedEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LibraryCheckFinishedEvent(Object source) {
        super(source);
    }
}
