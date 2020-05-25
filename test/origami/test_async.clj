(ns origami.test-async
  (:require
   [clojure.core.async :as async :refer :all]
   [clojure.string :as string]
   [opencv4.core :as cv]
   [opencv4.utils :as cvu]
   [origami.lazyseqs :as lazy]
  ;;  [opencv4.video :as video]
   [clojure.test :refer [deftest is]]))

(deftest async-mat
  (let [c (async/chan) l (lazy/folder-seq "resources-dev") log-file "target/testcv.log"]
    (async/go-loop []
      (let [x (async/<! c)]
        (spit log-file x :append true))
      (recur))
    (async/onto-chan c l)
    (async/close! c)
    (is (clojure.string/includes? (slurp log-file) "Mat"))))

(comment

  ;; on a webcam seq
  ;; 
  (def cam-channel (async/chan))
  (def w (lazy/webcam-seq))
  (def panel (cvu/imshow (cv/imread "resources-dev/marcel.jpg")))

  (async/go-loop []
    (let [x (async/<! cam-channel)]
      (cvu/re-show panel x))
    (recur))
  (async/onto-chan cam-channel w)

  ;;
  ;;
  )