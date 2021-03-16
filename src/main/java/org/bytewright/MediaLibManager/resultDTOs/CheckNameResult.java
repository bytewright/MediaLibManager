package org.bytewright.MediaLibManager.resultDTOs;

public class CheckNameResult extends CheckResult {
    public CheckNameResult(String absolutePath) {
        super(absolutePath);
    }

    @Override
    public String getResultLogLine() {
        return "name does not conform guidelines: " + getAbsolutePath();
    }

}
