package org.bytewright.MediaLibManager.resultDTOs;

import java.nio.file.Path;

public class CheckAudioQualityResult extends CheckResult {
    private final String audioStreamName;

    public CheckAudioQualityResult(Path path, String audioStreamName) {
        super(path.toAbsolutePath().toString());
        this.audioStreamName = audioStreamName;
    }

    @Override
    public String getResultLogLine() {
        return "This audiostream seems to be of bad quality: \"" + audioStreamName + "\" in file: " + getAbsolutePath();
    }
}
