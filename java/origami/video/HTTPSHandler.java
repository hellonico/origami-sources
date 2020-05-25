package origami.video;

import origami.Camera;
import origami.Origami;

public class HTTPSHandler {

    public static void main(String... args) {
        Origami.init();
        String url = "https://vod-progressive.akamaized.net/exp=1588505951~acl=%2A%2F460475708.mp4%2A~hmac=a309584c503691d9e59b696ba8b58cce2a763c1f956650b3c675230ec0f5a8d0/vimeo-prod-skyfire-std-us/01/590/0/2950529/460475708.mp4";
        new Camera().device(url).run();
    }
}
