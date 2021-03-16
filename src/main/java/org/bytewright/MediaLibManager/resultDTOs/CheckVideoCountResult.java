package org.bytewright.MediaLibManager.resultDTOs;

public class CheckVideoCountResult extends CheckResult {
    private final long videoCount;

    public CheckVideoCountResult(String absolutePath, long videos) {
        super(absolutePath);
        videoCount = videos;
    }

    @Override
    public String getResultLogLine() {
        return "Directory contains irregular video file count (" + videoCount + "): " + getAbsolutePath();
    }
}
