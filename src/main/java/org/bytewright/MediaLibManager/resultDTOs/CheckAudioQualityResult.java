package org.bytewright.MediaLibManager.resultDTOs;

public class CheckAudioQualityResult extends CheckResult {
    private final String audioStreamName;

    public CheckAudioQualityResult(String absolutePath, String audioStreamName) {
        super(absolutePath);
        this.audioStreamName = audioStreamName;
    }

    @Override
    public String getResultLogLine() {
        return "This audiostream seems to be of bad quality: \"" + audioStreamName + "\" in file: " + getAbsolutePath();
    }
}
