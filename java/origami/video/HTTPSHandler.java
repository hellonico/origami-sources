package origami.video;

import origami.Camera;
import origami.Origami;

public class HTTPSHandler {

    public static void main(String... args) {
        Origami.init();
        Origami.registerVideoHandler("http", new HttpVideoHandler());
        String url = "{:device \"http://www.hellonico.info/marcel/video-1617099899.mp4\" :slow 50}";
        new Camera().device(url).run();
    }
}
