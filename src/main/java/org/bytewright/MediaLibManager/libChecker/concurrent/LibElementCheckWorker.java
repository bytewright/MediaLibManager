package org.bytewright.MediaLibManager.libChecker.concurrent;

import org.bytewright.MediaLibManager.libChecker.LibElement;
import org.bytewright.MediaLibManager.libChecker.mediaConent.MCC_jaffree;
import org.bytewright.MediaLibManager.libChecker.mediaConent.MediaContentChecker;
import org.bytewright.MediaLibManager.resultDTOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LibElementCheckWorker extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibElementCheckWorker.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("(ae|oe|ue)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IS_VIDEO_PATTERN = Pattern.compile("(mkv|mp4|avi|mpg|vob)", Pattern.CASE_INSENSITIVE);
    private static final long FILE_SIZE_LIMIT = 12L * 1024L * 1024L * 1024L; // 10GB
    private static final FileFilter ONLY_VIDEOS_FILTER = LibElementCheckWorker::isVideoFile;
    private static final DirectoryStream.Filter<Path> ONLY_VIDEOS_FILTER2 = entry -> isVideoFile(entry.toFile());
    private final Consumer<CheckResult> resultCollector;
    private final Consumer<LibElement> taskConsumer;
    private final LibElement task;

    public LibElementCheckWorker(LibElement task, Consumer<LibElement> taskConsumer, Consumer<CheckResult> resultCollector) {
        this.task = task;
        this.taskConsumer = taskConsumer;
        this.resultCollector = resultCollector;
    }

    private static boolean isVideoFile(File pathname) {
        Optional<File> file = Optional.ofNullable(pathname);
        file = file.filter(LibElementCheckWorker::isMediaFile);
        Optional<String> extension = file.flatMap(LibElementCheckWorker::getFileExtension);
        extension = extension.filter(LibElementCheckWorker::isVideo);
        return extension.isPresent();
    }

    private static boolean isMediaFile(File file) {
        try {
            long size = Files.size(file.toPath());
            return size > 5 * 1024 * 1024; // greater 5 MB
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
        Path path = task.path();
        LOGGER.debug("Starting to check LibElement {}", path);
        try {
            if (Files.isDirectory(path)) {
                checkDirectory(path);
            } else {
                LOGGER.error("Can't check {} because its not a dir", path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDirectory(Path dirPath) throws IOException {
        checkName(dirPath);
        checkDirContents(dirPath);
        List<Path> videoFiles = Files.list(dirPath)
                .filter(path -> ONLY_VIDEOS_FILTER.accept(path.toFile()))
                .collect(Collectors.toList());
        checkFileSizes(videoFiles);
        checkMediaContent(videoFiles);
    }

    private void checkMediaContent(List<Path> videoFiles) {
        for (Path videoFile : videoFiles) {
            MediaContentChecker checker = new MCC_jaffree(videoFile);
            checker.performChecks(resultCollector);
        }
    }

    private void checkFileSizes(List<Path> videoFiles) {
        for (Path videoFile : videoFiles) {
            try {
                long bytes = Files.size(videoFile);
                if (bytes > FILE_SIZE_LIMIT) {
                    resultCollector.accept(new CheckFileSizeResult(videoFile.toString(), bytes));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkDirContents(Path dirPath) throws IOException {
        List<File> fileList = Files.list(dirPath)
                .map(Path::toFile)
                .collect(Collectors.toList());
        long videos = countVideos(fileList);
        if (videos > 1) {
            LOGGER.warn("Found more than video in dir {}", fileList);
            resultCollector.accept(new CheckVideoCountResult(dirPath.toString(), videos));
        } else if (videos == 0) {
            if (fileList.stream().noneMatch(File::isDirectory)) {
                LOGGER.warn("Found dir without any videos or subdirs {}", dirPath);
                resultCollector.accept(new CheckVideoCountResult(dirPath.toString(), videos));
            } else {
                fileList.stream()
                        .filter(File::isDirectory)
                        .map(File::toPath)
                        .map(LibElement::new)
                        .forEach(taskConsumer);
            }
        }
    }

    private long countVideos(List<File> fileList) {
        return fileList.stream()
                .filter(LibElementCheckWorker::isVideoFile)
                .count();
    }

    private void checkName(Path path) {
        String name = path.getFileName().toString();
        if (NAME_PATTERN.matcher(name).find()) {
            LOGGER.warn("Found name issue with {}", name);
            resultCollector.accept(new CheckNameResult(path.toString()));
        } else if (name.contains("  ")) {
            resultCollector.accept(new CheckNameDoubleSpacesResult(path.toString()));
        }
    }
}
