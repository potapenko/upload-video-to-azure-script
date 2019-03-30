(defproject upload-video-to-azure-script "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cider/cider-nrepl "0.15.1"]
                 [clj-time "0.14.4"]
                 [com.novemberain/monger "3.1.0"
                  :exclusions [com.google.guava/guava]]
                 [mount "0.1.13-SNAPSHOT"]
                 [org.clojure/clojure "1.10.0-alpha4"]
                 [cheshire "5.8.0"]
                 [cprop "0.1.10"]
                 [base64-clj "0.1.1"]
                 [camel-snake-kebab "0.4.0"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.clojure/core.async "0.4.474"]
                 [clj-http "3.9.0"]
                 [com.cognitect/transit-clj "0.8.309"]
                 [jarohen/chord "0.8.1"]
                 [com.taoensso/timbre "4.10.0"]]
  :repositories {"mvn"          "https://mvnrepository.com"
                 "jitpack"      "https://jitpack.io"}

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main playphraseme.phrases-extractor.core
  :profiles {:uberjar {:aot :all}})
