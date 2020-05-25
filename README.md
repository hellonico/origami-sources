# Origami Sources

## Handlers

Handlers are used to define a protocol that OpenCV does not recognize by default. So Your code can now access YouTube videos or https streams directly.

Here we define two protocols:
- **https://** to load videos directly from an https accessible video
- **youtube://** to stream videos directly 

### YouTube 

```clojure
(deftest youtube-test
  (let [_ (YouTubeHandler.)
        vc  (video/capture-device "youtube://PnqzVkPDUHQ")
        mat (cv/new-mat)]
    (dotimes [_ 100]
      (.grab vc)
      (.retrieve vc mat))
    (println mat)
    (is (= (cv/new-size 640 360) (cv/size mat)))))
```

### HTTPS

```clojure
(deftest https-test
  (let [_ (HttpVideoHandler.)
        url "https://raw.githubusercontent.com/hellonico/origami-sources/master/resources-dev/small.mp4"
        vc (video/capture-device url)
        mat (cv/new-mat)]
    (Thread/sleep 1000)
    (dotimes [_ 100]
      (.grab vc)
      (.retrieve vc mat))
    (is (= (cv/new-size 560 320) (cv/size mat)))))
```

## Lazy Sequences

Lazy Sequences prepare a list of object taken from a source, then load the OpenCV mats objects as required. 
This has the advantage that it does not load all the mats at the same time, and just load them when required.
The source list itself will be preloaded, so for example in a **folder-seq**, all the filenames of the folder will be pre-loaded.

### Zip Files

```clojure
(deftest zip-test
  (let [zip (lazy/zip-seq "resources-dev/photos.zip")]
    (is (not (nil? (first zip))))))
```

### Github Pictures

```clojure
(deftest github-test
  (let [url "https://github.com/hellonico/origami-fun/tree/master/resources/cat_photos"
        zip (lazy/github-seq url)]
    (is (not (nil? (first zip))))))
```

### Webcam 

```Clojure
(deftest webcam-test
  (let [cam (lazy/webcam-seq)]
    (is (not (nil? (first cam))))))
```

### Webpage

```clojure
(deftest webpage-test
  (let [ws (lazy/webpage-seq "https://blog.lingo24.com/short-phrases-help-connect-global-teams/")]
    (is (not (nil? (first ws))))))
```

### Pictures from a folder in Dropbox

```clojure
(deftest dropbox-test
  (let [ws (lazy/dropbox-seq)]
    (is (not (nil? (first ws))))))
```

### Pictures from a folder

```clojure
(deftest folder-test
  (let [ws (lazy/folder-seq "resources-dev/")]
    (is (not (nil? (first ws))))))
```

### Search on Flickr

```clojure
(deftest flickr-test
  (let [ws (lazy/flickr-seq ["cat"])]
    (is (not (nil? (first ws))))))
```

## Clojure core.async

This is extra, and shows how to combine lazy sequences and the usual Clojure core async channels.

### Process pictures of a folder via a channel

```clojure
(deftest async-mat
  (let [c (async/chan) 
        l (lazy/folder-seq "resources-dev") 
        log-file "target/testcv.log"]
    (async/go-loop []
      (let [x (async/<! c)]
        (spit log-file x :append true))
      (recur))
    (async/onto-chan c l)
    (Thread/sleep 500)
    (async/close! c)
    (is (clojure.string/includes? (slurp log-file) "Mat"))))
```

### Webcam Lazy sequence and core async

```clojure
(def cam-channel (async/chan))
(def w (lazy/webcam-seq))
(def panel (cvu/imshow (cv/imread "resources-dev/marcel.jpg")))

(async/go-loop []
  (let [x (async/<! cam-channel)]
    (cvu/re-show panel x))
  (recur))
(async/onto-chan cam-channel w)
```

