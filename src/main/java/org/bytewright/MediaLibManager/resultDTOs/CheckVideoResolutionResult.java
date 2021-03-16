package org.bytewright.MediaLibManager.resultDTOs;

public class CheckVideoResolutionResult extends CheckResult {
    private final Integer width;
    private final Integer height;

    public CheckVideoResolutionResult(String absolutePath, Integer width, Integer height) {
        super(absolutePath);
        this.width = width;
        this.height = height;
    }

    @Override
    public String getResultLogLine() {
        return "Found video with low resolution (" + width + "x" + height + "px) at: " + getAbsolutePath();
    }
}
