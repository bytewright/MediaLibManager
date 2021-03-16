package org.bytewright.MediaLibManager.resultDTOs;

public class CheckNameDoubleSpacesResult extends CheckResult {
    public CheckNameDoubleSpacesResult(String absolutePath) {
        super(absolutePath);
    }

    @Override
    public String getResultLogLine() {
        return "Found double whitespace in name: " + getAbsolutePath();
    }
}
