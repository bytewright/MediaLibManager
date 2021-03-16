package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.resultDTOs.CheckFileSizeResult;
import org.bytewright.MediaLibManager.resultDTOs.CheckNameResult;
import org.bytewright.MediaLibManager.resultDTOs.CheckResult;
import org.bytewright.MediaLibManager.resultDTOs.CheckVideoCountResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ResultTypesProvider {
    public Collection<Class<? extends CheckResult>> getResultTypes() {
        return List.of(CheckVideoCountResult.class, CheckNameResult.class, CheckFileSizeResult.class);
    }

    public List<CheckResult> sort(Class<? extends CheckResult> resultType, List<? extends CheckResult> checkResults) {
        if (resultType.equals(CheckFileSizeResult.class)) {
            Comparator<CheckFileSizeResult> cmp = Comparator.comparing(CheckFileSizeResult::getBytes).reversed();
            return checkResults.stream()
                    .map(checkResult -> (CheckFileSizeResult) checkResult)
                    .sorted(cmp)
                    .collect(Collectors.toList());
        } else {
            return checkResults.stream()
                    .sorted(Comparator.comparing(CheckResult::getResultLogLine))
                    .collect(Collectors.toList());
        }
    }
}
