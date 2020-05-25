(ns origami.test-handlers
  (:import [origami.video YouTubeHandler HttpVideoHandler])
  (:require
   [opencv4.core :as cv]
   [opencv4.video :as video]
   [clojure.test :refer [deftest is]]))

(deftest youtube-test
  (let [_ (YouTubeHandler.)
        vc  (video/capture-device "youtube://PnqzVkPDUHQ")
        mat (cv/new-mat)]
    (dotimes [_ 100]
      (.grab vc)
      (.retrieve vc mat))
    (println mat)
    (is (= (cv/new-size 640 360) (cv/size mat)))))

(deftest https-test
  (let [_ (HttpVideoHandler.)
        url "https://raw.githubusercontent.com/hellonico/origami-sources/master/resources-dev/small.mp4"
        vc (video/capture-device url)
        mat (cv/new-mat)]
    (Thread/sleep 1000)
    (dotimes [_ 100]
      (.grab vc)
      (.retrieve vc mat))
    (println mat)
    (is (= (cv/new-size 560 320) (cv/size mat)))))