(ns origami.lazyseqs
  (:require
   [clojure.string :as string]
   [clojure.java.io :as io]
   [opencv4.video :as vid]
   [opencv4.core :as cv]
   [opencv4.utils :as u])
  (:import
   [java.util.zip ZipFile]
   [org.jsoup Jsoup]
   [com.dropbox.core DbxRequestConfig]
   [com.dropbox.core.v2 DbxClientV2]
   [com.flickr4java.flickr.photos SearchParameters]
   [com.flickr4java.flickr REST Flickr]))

; ugly settings finder
(def settings-filename "settings.edn")
(def home-settings (str (System/getProperty "user.home") "/.origami/" settings-filename))
(def target-file 
  (cond 
    (.exist (clojure.java.io/as-file settings-filename)) (read-string (slurp home-settings))
    (.exist (clojure.java.io/as-file home-settings)) (read-string (slurp home-settings))
    :else (do (println "Using empty origami settings. Please set $HOME/.origami/settings.edn"){})))

(defn- lazy-mats [f coll]
  (lazy-seq
   (when-let [list (seq coll)]
     (cons (f (first list)) (lazy-seq (lazy-mats f (rest list)))))))

;
; FLICKR
(defn flickr-seq [tags]
  (let [flickr
        (Flickr. (-> settings :flickr :apiKey) (-> settings :flickr :sharedSecret) (REST.))
        p (.getPhotosInterface flickr)
        sp (SearchParameters.)
        _ (.setTags sp (into-array String tags))
        f (fn [p]
            (println "flickr [" (.getLargeUrl p) "]")
            (try (u/mat-from-url (.getLargeUrl p)) (catch Exception e (do (println "ERR:" p) (cv/new-mat) ))))
        list (lazy-seq (.search p sp 50 1))]
    (lazy-mats f list)))

;
; WEBCAM
(defn webcam-seq
  ([] (webcam-seq 0))
  ([device]
   (let [cam (vid/capture-device device)
         myf (fn [_] (let [buffer (cv/new-mat)] (.read cam buffer) buffer))]
     (lazy-mats myf (range)))))

;
; DROPBOX
(defn- meta-to-mat [files meta]
  (let [file-meta (.download files (.getPathDisplay meta))
        f (java.io.File/createTempFile "origami" "jpg")
        fos (java.io.FileOutputStream. f)]
    (.download file-meta fos)
    (.close fos)
    (cv/imread (.getAbsolutePath f))))

(defn dropbox-seq
  ([] (dropbox-seq ""))
  ([path]
   (let [config (.build (DbxRequestConfig/newBuilder "origami"))
         client (DbxClientV2. config (-> settings :dropbox :accessToken))
         files (.files client)
         res (.listFolder files path)
         _list (seq (.getEntries res))
         list (filter #(string/includes? (.getPathDisplay %) "jpg") _list)
         myfn (fn [meta] (meta-to-mat files meta))
         ]
     ;(println _list)
     (lazy-mats myfn list))))

;
; FOLDER
;
(defn- list-folder [ext folder]
  (->>   folder
         (io/as-file)
         (.listFiles)
         (filter #(string/includes? (string/lower-case (.getName %)) ext))))

(defn folder-seq
  ([] (folder-seq "jpg" "." cv/IMREAD_UNCHANGED))
  ([path] (folder-seq "jpg" path cv/IMREAD_UNCHANGED))
  ([ext path flag]
   (let [list (list-folder ext path)
         myfn (fn [file] (cv/imread (.getAbsolutePath file) flag))]
     (lazy-mats myfn list))))

;
; ZIP
(defn zip-seq [filename]
  (let [zip (ZipFile. filename)
        entries (iterator-seq (.entries zip))
        list (filter #(re-matches #".*(?i)[JPG|PNG|JPEG|GIF]" (.getName %)) entries)
        myfn #(let [f (java.io.File/createTempFile "origami" "jpg")]
                (clojure.java.io/copy (.getInputStream zip %) f)
                (cv/imread (.getAbsolutePath f)))]
    (lazy-mats myfn list)))

;
; WEBPAGE
; 
(defn webpage-seq
  ([url] (webpage-seq url "img[src~=(?i)\\.(png|jpe?g|gif)]"))
  ([url selector]
   (let [doc (.get (Jsoup/connect url))
         imgs (.select doc selector)
         list (map #(.attr % "src") imgs)
         myfn (fn [src] (u/mat-from-url src))]
    ;;  (println list)
     (lazy-mats myfn list))))

;
; GITHUB
;
(defn github-seq [url]
 (let [
       selector "a[href~=(?i)\\.(png|jpe?g|gif)]"
       doc (.get (Jsoup/connect url))
       list (.select doc selector)
       myfn (fn[a] 
          (u/mat-from-url (string/replace (str "https://raw.githubusercontent.com/" (.attr a "href")) "/blob" "")))
       ]
   (lazy-mats myfn list)))

;
; IDEAS AND IN PROGRESS
;

;
; VIDEO STREAMS 
; READ a frame every n frame
; 

; IMGUR
; https://imgur.com/
; https://github.com/kskelm/baringo