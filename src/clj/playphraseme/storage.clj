(ns playphraseme.storage
  (:refer-clojure :exclude [list])
  (:require [playphraseme.azure-blob :as azure]
            [playphraseme.config :refer [env]]
            [playphraseme.queries.phrases :as phrases]
            [mount.core :as mount]))

(def cnt (azure/blob-container
          {:storage-account-name "playphraseme8video"
           :storage-account-key  "vZm7fDaoW5LSgGduwCg/+9Z0jqvPJF1Ct1gO0HxVJtvfHPE6LoQVSZrAZV9HvtNQ5O69uxl8YI9cBGlw9TCOlg=="
           :storage-container    "media"}))

(defn mark-phrase-uploaded [phrase]
  (phrases/update-phrase!
   (assoc phrase :state 1)))

(defn mark-not-have-video [phrase]
  (phrases/update-phrase!
   (assoc phrase :have-video false)))

(defn upload-phrase-video [{:keys [id movie] :as phrase}]
  (let [{:keys [phrases-dir]} env
        path                  (format "%s/%s.mp4" movie id)
        res                   (azure/upload-blob
                               cnt path (str phrases-dir "/" path))]
    (println "uploaded video file:" path "->" res)
    (when (= :ok res)
      (mark-phrase-uploaded phrase))))

(defn sync-video []
  (let [{:keys [parallel]} env]
   (loop []
     (let [phrases (phrases/find-phrases {:have-video true
                                          :state      nil} 0 10)]
       (when-not (empty? phrases)
         (->> phrases
              (partition-all parallel)
              (map (fn [part]
                     (->> part (map upload-phrase-video)))))
         #_(recur))))))

(comment
  (mount/start)

  (sync-video)


  (azure/list cnt)


  )
