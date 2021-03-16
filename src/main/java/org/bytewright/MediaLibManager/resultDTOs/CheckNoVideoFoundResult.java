package org.bytewright.MediaLibManager.resultDTOs;

public class CheckNoVideoFoundResult extends CheckResult {
    public CheckNoVideoFoundResult(String absolutePath) {
        super(absolutePath);
    }

    @Override
    public String getResultLogLine() {
        return "Failed to find any videostream in " + getAbsolutePath();
    }
}
