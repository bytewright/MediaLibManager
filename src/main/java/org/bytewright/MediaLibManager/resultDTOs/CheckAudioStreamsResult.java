package org.bytewright.MediaLibManager.resultDTOs;

public class CheckAudioStreamsResult extends CheckResult {
    private final String languageFound;

    public CheckAudioStreamsResult(String absolutePath, String languageFound) {
        super(absolutePath);
        this.languageFound = languageFound;
    }

    @Override
    public String getResultLogLine() {
        return "Found only one audio stream (lang: " + languageFound + ") in videofile: " + getAbsolutePath();
    }
}
