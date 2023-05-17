package origami.video;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.Extension;
import com.github.kiulian.downloader.model.Filter;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import org.opencv.videoio.Videoio;
import origami.Camera;
import origami.Origami;

import java.io.File;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;

public class YouTubeHandler implements VideoHandler {
    static {
        Origami.registerVideoHandler("youtube", new YouTubeHandler());
    }

    YoutubeDownloader downloader = new YoutubeDownloader();

    public YouTubeHandler() {

    }

    public List<Format> retrieveInfo(String videoId) {

// sync parsing
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data();
//
//// async parsing
//        RequestVideoInfo request = new RequestVideoInfo(videoId)
//                .callback(new YoutubeCallback<VideoInfo>() {
//                    @Override
//                    public void onFinished(VideoInfo videoInfo) {
//                        System.out.println("Finished parsing");
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        System.out.println("Error: " + throwable.getMessage());
//                    }
//                })
//                .async();
//        Response<VideoInfo> response = downloader.getVideoInfo(request);
//        VideoInfo video = response.data(); // will block thread

// video details
        VideoDetails details = video.details();
        System.out.println(details.title());
        System.out.println(details.viewCount());
        details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

// HLS url only for live videos and streams
        if (video.details().isLive()) {
            System.out.println("Live Stream HLS URL: " + video.details().liveUrl());
        }

// get videos formats only with audio
        List<VideoWithAudioFormat> videoWithAudioFormats = video.videoWithAudioFormats();
        videoWithAudioFormats.forEach(it -> {
            System.out.println(it.audioQuality() + ", " + it.videoQuality() + " : " + it.url());
        });

// get all videos formats (may contain better quality but without audio)
        List<VideoFormat> videoFormats = video.videoFormats();
        videoFormats.forEach(it -> {
            System.out.println(it.videoQuality() + " : " + it.url());
        });

// get audio formats
        List<AudioFormat> audioFormats = video.audioFormats();
        audioFormats.forEach(it -> {
            System.out.println(it.audioQuality() + " : " + it.url());
        });

// get best format
        video.bestVideoWithAudioFormat();
        video.bestVideoFormat();
        video.bestAudioFormat();

// filtering formats
        List<Format> formats = video.findFormats(new Filter<Format>() {
            @Override
            public boolean test(Format format) {
                return format.extension() == Extension.MPEG4;
            }
        });

        return formats;
//
//// itags can be found here - https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
//        Format formatByItag = video.findFormatByItag(18); // return null if not found
//        if (formatByItag != null) {
//            System.out.println(formatByItag.url());
//        }
    }

    public String getFilename(String _url) {


        String videoName = _url.replaceAll("youtube://","");
        File outputDir = new File("my_videos");
        Format format = this.retrieveInfo(videoName).get(0);

// sync downloading
//        RequestVideoFileDownload request = new RequestVideoFileDownload(format)
//                // optional params
//                //.saveTo(outputDir) // by default "videos" directory
//                .renameTo(videoName) // by default file name will be same as video title on youtube
//                .overwriteIfExists(true); // if false and file with such name already exits sufix will be added video(1).mp4
//        Response<File> response = downloader.downloadVideoFile(request);
//        File data = response.data();

// async downloading with callback
        RequestVideoFileDownload request = new RequestVideoFileDownload(format)
                .renameTo(videoName) // by default file name will be same as video title on youtube
                .overwriteIfExists(true) // if false and file with such name already exits sufix will be added video(1).mp4
                .callback(new YoutubeProgressCallback<File>() {
                    @Override
                    public void onDownloading(int progress) {
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo) {
                        System.out.println("Finished file: " + videoInfo);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                })
                .async();
        String output = request.getOutputFile().getAbsolutePath();
        System.out.println("This is the output path:"+output);
        if(request.getOutputFile().exists()) {
            return output;
        }

        Response<File> response = downloader.downloadVideoFile(request);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return output;

//        File data = response.data(); // will block current thread
//        return data.getAbsolutePath();
//
//// async downloading without callback
//        RequestVideoFileDownload request = new RequestVideoFileDownload(format).async();
//        Response<File> response = downloader.downloadVideoFile(request);
//        File data = response.data(20, TimeUnit.SECONDS); // will block current thread and may throw TimeoutExeption
//
//// download in-memory to OutputStream
//        OutputStream os = new ByteArrayOutputStream();
//        RequestVideoStreamDownload request = new RequestVideoStreamDownload(format, os);
//        Response<Void> response = downloader.downloadVideoStream(request);
    }

    public static void main(String[] args) throws InterruptedException {
        Origami.init();

        Camera cam = new Camera();
        cam.device("youtube://YQHsXMglC9A");
        origami.Filter p = mat -> {
            cvtColor(mat, mat, COLOR_BGR2GRAY);
            cvtColor(mat, mat, COLOR_GRAY2BGR);
            return mat;
        };
        cam.filter(p);
        cam.slowDown(100);
        cam.run();


    }

}
