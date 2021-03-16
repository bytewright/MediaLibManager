package org.bytewright.MediaLibManager.resultDTOs;

public abstract class CheckResult {
    protected String absolutePath;

    public CheckResult(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public abstract String getResultLogLine();
}
