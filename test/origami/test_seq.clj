(ns origami.test-seq
  (:require
   [origami.lazyseqs :as lazy]
   [clojure.test :refer [deftest is]]))


(deftest zip-test
  (let [zip (lazy/zip-seq "resources-dev/photos.zip")]
    (is (not (nil? (first zip))))))

(deftest github-test
  (let [url "https://github.com/hellonico/origami-fun/tree/master/resources/cat_photos"
        zip (lazy/github-seq url)]
    (is (not (nil? (first zip))))))

(deftest webcam-test
  (let [cam (lazy/webcam-seq)]
    (is (not (nil? (first cam))))))

(deftest webpage-test
  (let [ws (lazy/webpage-seq "https://blog.lingo24.com/short-phrases-help-connect-global-teams/")]
    (is (not (nil? (first ws))))))

(deftest dropbox-test
  (let [ws (lazy/dropbox-seq)]
    (is (not (nil? (first ws))))))

(deftest folder-test
  (let [ws (lazy/folder-seq "resources-dev/")]
    (is (not (nil? (first ws))))))

(deftest flickr-test
  (let [ws (lazy/flickr-seq ["cat"])]
    (is (not (nil? (first ws))))))


(comment

  ;(def ws (lazy/dropbox-seq))

  ;(u/imshow (first ws))
  )

;;   (->> ["猫"]
;;        (flickr-seq)
;;        (map-indexed (fn [idx itm] (cv/imwrite itm (str "flickr/" idx ".jpg"))))))