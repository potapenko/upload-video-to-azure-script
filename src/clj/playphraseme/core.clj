(ns playphraseme.core
  (:gen-class)
  (:require [taoensso.timbre :as log]
            [mount.core :as mount]))

(defn start-app [args]
  (log/info "Uploader started!")
  (mount/start))

(defn -main [& args]
  (start-app args))
