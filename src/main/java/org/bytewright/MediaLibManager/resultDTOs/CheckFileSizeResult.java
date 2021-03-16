package org.bytewright.MediaLibManager.resultDTOs;

import java.text.DecimalFormat;

public class CheckFileSizeResult extends CheckResult {
    private long bytes;

    public CheckFileSizeResult(String absolutePath, long bytes) {
        super(absolutePath);
        this.bytes = bytes;
    }

    @Override
    public String getResultLogLine() {
        return "Found large file (" + toGB(bytes) + ") at: " + getAbsolutePath();
    }

    public long getBytes() {
        return bytes;
    }

    private String toGB(long bytes) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        double gbBytes = ((double) bytes) / 1024.0 / 1024.0 / 1024.0;
        return df.format(gbBytes) + " GB";
    }
}
