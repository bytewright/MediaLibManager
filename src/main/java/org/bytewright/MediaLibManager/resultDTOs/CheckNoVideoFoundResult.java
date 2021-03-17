package org.bytewright.MediaLibManager.resultDTOs;

import java.nio.file.Path;

public class CheckNoVideoFoundResult extends CheckResult {
    public CheckNoVideoFoundResult(Path path) {
        super(path.toAbsolutePath().toString());
    }

    @Override
    public String getResultLogLine() {
        return "Failed to find any videostream in " + getAbsolutePath();
    }
}
