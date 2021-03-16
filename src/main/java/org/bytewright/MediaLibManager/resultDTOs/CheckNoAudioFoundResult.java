package org.bytewright.MediaLibManager.resultDTOs;

import org.bytewright.MediaLibManager.resultDTOs.CheckResult;

public class CheckNoAudioFoundResult extends CheckResult {
    public CheckNoAudioFoundResult(String absolutePath) {
        super(absolutePath);
    }

    @Override
    public String getResultLogLine() {
        return "Couldn't find any audiostream in file " + getAbsolutePath();
    }
}
