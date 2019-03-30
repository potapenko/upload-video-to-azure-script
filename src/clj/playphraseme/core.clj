(ns playphraseme.core
  (:gen-class)
  (:require [taoensso.timbre :as log]
            [mount.core :as mount]
            [playphraseme.storage :as storage]))

(defn start-app [args]
  (log/info "Uploader started!")
  (mount/start)
  (storage/start))

(defn -main [& args]
  (start-app args))
