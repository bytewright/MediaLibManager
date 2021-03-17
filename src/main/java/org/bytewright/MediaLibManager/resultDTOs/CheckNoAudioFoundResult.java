package org.bytewright.MediaLibManager.resultDTOs;

import org.bytewright.MediaLibManager.resultDTOs.CheckResult;

import java.nio.file.Path;

public class CheckNoAudioFoundResult extends CheckResult {
    public CheckNoAudioFoundResult(Path path) {
        super(path.toAbsolutePath().toString());
    }

    @Override
    public String getResultLogLine() {
        return "Couldn't find any audiostream in file " + getAbsolutePath();
    }
}
