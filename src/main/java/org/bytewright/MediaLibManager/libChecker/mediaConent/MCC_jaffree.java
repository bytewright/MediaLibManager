package org.bytewright.MediaLibManager.libChecker.mediaConent;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.github.kokorin.jaffree.ffprobe.Tag;
import org.bytewright.MediaLibManager.resultDTOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MCC_jaffree implements MediaContentChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MCC_jaffree.class);
    private static final Pattern IS_BAD_AUDIO_QUALI_PATTERN = Pattern.compile("(mic)", Pattern.CASE_INSENSITIVE);
    private final File videoFile;

    public MCC_jaffree(File videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public void performChecks(Consumer<CheckResult> resultCollector) {
        String pathToVideo = videoFile.getAbsolutePath();
        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();
        List<Stream> streams = result.getStreams();
        Map<StreamType, List<Stream>> collect = streams.stream()
                .collect(Collectors.toMap(Stream::getCodecType, List::of, this::mergeLists));
        LOGGER.trace("File {} contains {} streams", pathToVideo, streams.size());
        List<Stream> audioStreams = collect.get(StreamType.AUDIO);
        checkAudioStreams(resultCollector, audioStreams);
        List<Stream> videoStreams = collect.get(StreamType.VIDEO);
        checkVideoStream(resultCollector, videoStreams);
    }

    private List<Stream> mergeLists(List<Stream> u, List<Stream> u1) {
        return java.util.stream.Stream.concat(u.stream(), u1.stream()).collect(Collectors.toList());
    }

    private void checkAudioStreams(Consumer<CheckResult> resultCollector, List<Stream> audioStreams) {
        if (audioStreams == null || audioStreams.isEmpty()) {
            resultCollector.accept(new CheckNoAudioFoundResult(videoFile.getAbsolutePath()));
            return;
        }
        LOGGER.debug("Found {} audio streams in file {}", audioStreams.size(), videoFile);
        Map<String, String> audioLanguages = new HashMap<>();
        for (Stream audioStream : audioStreams) {
            Optional<String> language = Optional.ofNullable(audioStream.getTag("language"));
            if (language.isEmpty()) {
                LOGGER.warn("Failed to parse language tag from audiostream of file {}: {}", videoFile, parseFail("", audioStream.getTags()));
                continue;
            }
            String title = Optional.ofNullable(audioStream.getTag("title")).orElse("NO_TITLE_FOUND");
            audioLanguages.put(language.get(), title);
        }
        LOGGER.debug("Found tags on audio stream: {}", audioLanguages);
        if (audioLanguages.size() == 1) {
            String languageFound = audioLanguages.keySet().stream().findFirst().orElse("FAIL");
            resultCollector.accept(new CheckAudioStreamsResult(videoFile.getAbsolutePath(), languageFound));
        } else {
            audioLanguages.values().stream()
                    .filter(this::hasBadAudioQuality)
                    .findAny()
                    .map(s -> new CheckAudioQualityResult(videoFile.getAbsolutePath(), s))
                    .ifPresent(resultCollector);
        }
    }

    private boolean hasBadAudioQuality(String audioStreamName) {
        return IS_BAD_AUDIO_QUALI_PATTERN.matcher(audioStreamName).find();
    }

    private String parseFail(String prefix, List<Tag> tags) {
        return prefix + tags.stream()
                .map(tag -> tag.getKey() + ":" + tag.getValue())
                .collect(Collectors.joining(" ; "));
    }

    private void checkVideoStream(Consumer<CheckResult> resultCollector, List<Stream> videoStreams) {
        if (videoStreams == null || videoStreams.isEmpty()) {
            resultCollector.accept(new CheckNoVideoFoundResult(videoFile.getAbsolutePath()));
            return;
        }
        Stream videoStream = videoStreams.get(0);
        Integer width = videoStream.getWidth();
        Integer height = videoStream.getHeight();
        String codecName = videoStream.getCodecName();
        LOGGER.debug("Found {} video streams in file {}, first has resolution and codec: {}x{}px {}",
                videoStreams.size(), videoFile, width, height, codecName);
        if (width+height<1500) {
            resultCollector.accept(new CheckVideoResolutionResult(videoFile.getAbsolutePath(), width, height));
        }
    }
}
