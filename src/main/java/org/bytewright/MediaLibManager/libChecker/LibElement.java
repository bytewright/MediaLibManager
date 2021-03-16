package org.bytewright.MediaLibManager.libChecker;

import org.bytewright.MediaLibManager.libChecker.mediaConent.MCC_jaffree;
import org.bytewright.MediaLibManager.libChecker.mediaConent.MediaContentChecker;
import org.bytewright.MediaLibManager.resultDTOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LibElement implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibElement.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("(ae|oe|ue)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IS_VIDEO_PATTERN = Pattern.compile("(mkv|mp4|avi|mpg|vob)", Pattern.CASE_INSENSITIVE);
    private static final long FILE_SIZE_LIMIT = 12L * 1024L * 1024L * 1024L; // 10GB
    private static final FileFilter ONLY_VIDEOS_FILTER = LibElement::isVideoFile;
    private final File libElement;
    private Consumer<CheckResult> resultCollector;

    public LibElement(File libElement) {
        this.libElement = libElement;
    }

    private static boolean isVideoFile(File pathname) {
        Optional<File> file = Optional.ofNullable(pathname);
        file = file.filter(LibElement::isMediaFile);
        Optional<String> extension = file.flatMap(LibElement::getFileExtension);
        extension = extension.filter(LibElement::isVideo);
        return extension.isPresent();
    }

    private static boolean isMediaFile(File file) {
        try {
            long size = Files.size(file.toPath());
            return size > 10 * 1024 * 1024; // greater 10 MB
        } catch (IOException e) {
        }
        return false;
    }

    private static Optional<String> getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return Optional.empty();
        }
        return Optional.of(name.substring(lastIndexOf));
    }

    private static boolean isVideo(String fileName) {
        return IS_VIDEO_PATTERN.matcher(fileName).find();
    }

    @Override
    public void run() {
        LOGGER.debug("Starting to check element '{}'", libElement);
        if (libElement.isDirectory()) {
            checkName(libElement);
            checkDirContents(libElement);
            List<File> videoFiles = Arrays.stream(libElement.listFiles(ONLY_VIDEOS_FILTER))
                    .collect(Collectors.toList());
            checkFileSizes(videoFiles);
            checkMediaContent(videoFiles);

        }
    }

    private void checkMediaContent(List<File> videoFiles) {
        for (File videoFile : videoFiles) {
            MediaContentChecker checker = new MCC_jaffree(videoFile);
            checker.performChecks(resultCollector);
        }
    }

    private void checkFileSizes(List<File> videoFiles) {
        for (File file : videoFiles) {
            try {
                long bytes = Files.size(file.toPath());
                if (bytes > FILE_SIZE_LIMIT) {
                    resultCollector.accept(new CheckFileSizeResult(file.getAbsolutePath(), bytes));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkDirContents(File file) {
        List<File> fileList = Arrays.stream(file.listFiles()).collect(Collectors.toList());
        long videos = countVideos(fileList);
        if (videos > 1) {
            LOGGER.warn("Found more than video in dir {}", fileList);
            resultCollector.accept(new CheckVideoCountResult(file.getAbsolutePath(), videos));
        } else if (videos == 0 && fileList.stream().noneMatch(File::isDirectory)) {
            LOGGER.warn("Found dir without any videos or subdirs {}", file);
            resultCollector.accept(new CheckVideoCountResult(file.getAbsolutePath(), videos));
        }
    }

    private long countVideos(List<File> fileList) {
        return fileList.stream()
                .filter(LibElement::isVideoFile)
                .count();
    }

    private void checkName(File file) {
        String name = file.getName();
        if (NAME_PATTERN.matcher(name).find()) {
            LOGGER.warn("Found name issue with {}", name);
            resultCollector.accept(new CheckNameResult(file.getAbsolutePath()));
        } else if (name.contains("  ")) {
            resultCollector.accept(new CheckNameDoubleSpacesResult(file.getAbsolutePath()));
        }
    }

    public void registerCollector(Consumer<CheckResult> resultCollector) {
        this.resultCollector = resultCollector;
    }
}
