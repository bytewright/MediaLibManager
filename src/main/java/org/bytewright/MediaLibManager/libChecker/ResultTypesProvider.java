package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.resultDTOs.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResultTypesProvider {
    public Collection<Class<? extends CheckResult>> getResultTypes() {
        return List.of(
                CheckVideoCountResult.class,
                CheckNameResult.class,
                CheckFileSizeResult.class,
                CheckNoAudioFoundResult.class,
                CheckAudioStreamsResult.class,
                CheckAudioQualityResult.class,
                CheckNoVideoFoundResult.class,
                CheckVideoResolutionResult.class,
                CheckNameDoubleSpacesResult.class
        );
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
