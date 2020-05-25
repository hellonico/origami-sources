(defproject origami-sources "0.1.0-SNAPSHOT"
  :java-source-paths ["java"]
  :plugins [[lein-auto "0.1.3"]]
  :auto {:default {:file-pattern #"\.(java)$"}}
  :repositories [["jitpack" "https://jitpack.io/"]
                 ["vendredi" "https://repository.hellonico.info/repository/hellonico/"]]
  :dependencies [[org.clojure/clojure "1.10.0" :scope "provided"]
                 [com.dropbox.core/dropbox-core-sdk "3.1.3"]
                 [com.flickr4java/flickr4java "3.0.4"]
                 [org.jsoup/jsoup "1.10.2"]
                 [com.github.Commit451/YouTubeExtractor "6.0.0"]
                 [org.clojure/core.async "1.2.603" :scope "provided"]
                 [origami/origami "4.3.0-7"]])
