package org.bytewright.MediaLibManager.resultDTOs;

import java.nio.file.Path;

public class CheckAudioStreamsResult extends CheckResult {
    private final String languageFound;

    public CheckAudioStreamsResult(Path path, String languageFound) {
        super(path.toAbsolutePath().toString());
        this.languageFound = languageFound;
    }

    @Override
    public String getResultLogLine() {
        return "Found only one audio stream (lang: " + languageFound + ") in videofile: " + getAbsolutePath();
    }
}
