package org.bytewright.MediaLibManager.libChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DirSource {
    private static final String FILEPATH_OPTION_NAME = "DirToCheck";
    @Autowired
    ApplicationArguments applicationArguments;

    public Set<Path> getDirectories() {
        if (!applicationArguments.containsOption(FILEPATH_OPTION_NAME)) {
            throw new IllegalArgumentException(getExceptiontext());
        }
        List<String> filePath = applicationArguments.getOptionValues(FILEPATH_OPTION_NAME);
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException(getExceptiontext());
        }
        return filePath.stream()
                .map(Path::of)
                .collect(Collectors.toSet());
    }

    private String getExceptiontext() {
        String text = "provide at least one dir with argument name " + FILEPATH_OPTION_NAME;
        return text + " For example: --DirToCheck=\\\\\\\\networkdrive\\\\foldername";
    }
}
