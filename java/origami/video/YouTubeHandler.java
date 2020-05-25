package origami.video;

import com.commit451.youtubeextractor.Stream;
import com.commit451.youtubeextractor.YouTubeExtraction;
import com.commit451.youtubeextractor.YouTubeExtractor;
import origami.Origami;
import origami.Camera;
import origami.utils.Downloader;

public class YouTubeHandler implements VideoHandler {
    static {
        Origami.registerVideoHandler("youtube", new YouTubeHandler());
    }

    private YouTubeExtractor extractor;

    public YouTubeHandler() {
        extractor = new YouTubeExtractor.Builder().build();
    }

    public String getFilename(String _url) {
        String id = _url.substring(_url.indexOf("://") + 3);
        int index = 0;
        if (id.contains("/")) {
            index = Integer.parseInt(id.substring(id.indexOf("/") + 1));
        }
        YouTubeExtraction yte = extractor.extract(id).blockingGet();
        Stream.VideoStream vs = (Stream.VideoStream) yte.getStreams().get(index);
        String url = vs.getUrl();
        String title = yte.getTitle();
        System.out.printf("Downloading: [%s]", url);

        if (title.contains("/")) {
            title = title.substring(title.lastIndexOf("/") + 1);
        }
        String filename = title + ".mp4";
        new Thread(() -> {
            Downloader.asyncTransfer(url, filename);
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        return filename;
    }

    public static void main(String[] args) throws InterruptedException {
        Origami.init();
        final String input = args.length > 0 ? args[0] : "youtube://PnqzVkPDUHQ";
        VideoCapture vc = (VideoCapture) Origami.CaptureDevice("youtube://PnqzVkPDUHQ");
        System.out.println(vc.grab());
//
//        new Thread(() -> {
//            // WatchedFilter wf = new WatchedFilter();
//            // wf.setFilePath("filters.edn");
//            new Camera().device(input).run();
//        }).start();

    }

}
