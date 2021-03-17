package org.bytewright.MediaLibManager.resultDTOs;

import java.nio.file.Path;

public class CheckVideoResolutionResult extends CheckResult {
    private final Integer width;
    private final Integer height;

    public CheckVideoResolutionResult(Path path, Integer width, Integer height) {
        super(path.toAbsolutePath().toString());
        this.width = width;
        this.height = height;
    }

    @Override
    public String getResultLogLine() {
        return "Found video with low resolution (" + width + "x" + height + "px) at: " + getAbsolutePath();
    }
}
