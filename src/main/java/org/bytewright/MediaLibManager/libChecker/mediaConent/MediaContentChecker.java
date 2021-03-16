package org.bytewright.MediaLibManager.libChecker.mediaConent;

import org.bytewright.MediaLibManager.libChecker.LibElement;
import org.bytewright.MediaLibManager.resultDTOs.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public interface MediaContentChecker {
    void performChecks(Consumer<CheckResult> resultCollector);
}
