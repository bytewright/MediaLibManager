//package org.bytewright.MediaLibManager.libChecker.mediaConent;
//
//import org.bytedeco.ffmpeg.avcodec.AVCodec;
//import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
//import org.bytedeco.ffmpeg.avformat.AVFormatContext;
//import org.bytedeco.ffmpeg.avformat.AVStream;
//import org.bytedeco.ffmpeg.global.avcodec;
//import org.bytedeco.ffmpeg.global.avformat;
//import org.bytedeco.ffmpeg.global.avutil;
//import org.bytedeco.javacpp.PointerPointer;
//import org.bytewright.MediaLibManager.resultDTOs.CheckResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.function.Consumer;
//
///**
// * based on:
// <dependency>
// <groupId>org.bytedeco</groupId>
// <artifactId>ffmpeg-platform</artifactId>
// <version>4.3.2-1.5.5</version>
// </dependency>
// */
//public class MCC_ffmpegPlatform implements MediaContentChecker {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MCC_ffmpegPlatform.class);
//    private final File videoFile;
//
//    public MCC_ffmpegPlatform(File videoFile) {
//        this.videoFile = videoFile;
//    }
//
//    @Override
//    public void performChecks(Consumer<CheckResult> resultCollector) {
//        String absolutePath = videoFile.getAbsolutePath();
//        AVFormatContext formatContext = new AVFormatContext(null);
//        int retVal = avformat.avformat_open_input(formatContext, absolutePath, null, null);
//        if (retVal < 0) {
//            LOGGER.error("avformat_open_input had return value of {} for file: {}", retVal, absolutePath);
//        }
//        retVal = avformat.avformat_find_stream_info(formatContext, (PointerPointer) null);
//        if (retVal < 0) {
//            LOGGER.error("avformat_find_stream_info had return value of {} for file: {}", retVal, absolutePath);
//        }
//        for (int i = 0; i < formatContext.nb_streams(); i++) {
//            AVStream stream = formatContext.streams(i);
//            AVCodecParameters codecpar = stream.codecpar();
//            if (codecpar.codec_type() == avutil.AVMEDIA_TYPE_AUDIO) {
//                AVCodec avCodec = avcodec.avcodec_find_decoder(codecpar.codec_id());
//                LOGGER.info("Found audio stream {}: {}", codecpar, avCodec);
//            } else if (codecpar.codec_type() == avutil.AVMEDIA_TYPE_VIDEO) {
//                int width = codecpar.width();
//                int height = codecpar.height();
//                LOGGER.info("Found video stream: {} with resolution {}x{}", codecpar, width, height);
//            }
//        }
//        LOGGER.warn("Found: {}", formatContext);
//    }
//}
