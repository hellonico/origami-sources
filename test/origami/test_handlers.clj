(ns origami.test-handlers
  (:import [origami.video YouTubeHandler HttpVideoHandler])
  (:require
    [opencv4.core :as cv]
    [opencv4.video :as video]
   [clojure.test :refer [deftest is]]))

(deftest youtube-test
  (let[_ (YouTubeHandler.) vc  (video/capture-device "youtube://PnqzVkPDUHQ") mat (cv/new-mat)]
    (.retrieve vc mat)
    (println mat)
             (is (not (nil? mat)))
    ))


(deftest youtube-test
  (let[_ (HttpVideoHandler.)
       url "https://vod-progressive.akamaized.net/exp=1588505951~acl=%2A%2F460475708.mp4%2A~hmac=a309584c503691d9e59b696ba8b58cce2a763c1f956650b3c675230ec0f5a8d0/vimeo-prod-skyfire-std-us/01/590/0/2950529/460475708.mp4"
       vc (video/capture-device url) mat (cv/new-mat)]
    (.retrieve vc mat)
             (println mat)
             (is (not (nil? mat)))
             ))