(ns playphraseme.storage
  (:refer-clojure :exclude [list])
  (:require [playphraseme.azure-blob :as azure]
            [playphraseme.config :refer [env]]
            [playphraseme.queries.phrases :as phrases]
            [mount.core :as mount]
            [taoensso.timbre :as log]
            [clojure.java.io :as io]))

(def cnt (azure/blob-container
          {:storage-account-name "playphraseme8video"
           :storage-account-key  "vZm7fDaoW5LSgGduwCg/+9Z0jqvPJF1Ct1gO0HxVJtvfHPE6LoQVSZrAZV9HvtNQ5O69uxl8YI9cBGlw9TCOlg=="
           :storage-container    "media"}))

(defn mark-phrase-uploaded [phrase]
  (phrases/update-phrase!
   (assoc phrase :state 1)))

(defn mark-not-have-video [{:keys [id] :as phrase}]
  (println "Not have video!" id)
  (phrases/update-phrase!
   (assoc phrase :have-video false)))

(defn upload-phrase-video [{:keys [id movie] :as phrase}]
  (let [{:keys [phrases-dir]} env
        blob-path             (format "%s/%s.mp4" movie id)
        local-path            (str phrases-dir "/" blob-path)]
    (if (-> (io/file local-path) .exists)
      (let [res (azure/upload-blob cnt blob-path local-path)]
          (println "uploaded video file:" blob-path "->" res)
          (when (= :ok res)
            (mark-phrase-uploaded phrase)))
        (mark-not-have-video phrase))))

(defn sync-video []
  (let [{:keys [parallel]} env]
   (loop []
     (try
      (let [phrases (phrases/find-phrases {:have-video true
                                           :state      nil} 0 10)]
        (when-not (empty? phrases)
          (->> phrases
               (partition-all parallel)
               (map (fn [part]
                      (->> part (map upload-phrase-video)))))
          #_(recur)))
      (catch Exception e
        (println "Error:" e)
        (Thread/sleep 10000))))))

(defn start []
  (mount/start)
  (sync-video))

(comment

  (start)

  (phrases/count-phrases {:have-video true :state 1})

  (azure/list cnt)

  )
