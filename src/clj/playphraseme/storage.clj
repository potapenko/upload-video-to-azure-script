(ns playphraseme.storage
  (:refer-clojure :exclude [list])
  (:require [playphraseme.azure-blob :as azure]))

(def cnt (azure/blob-container
          {:storage-account-name "playphraseme8video"
           :storage-account-key  "vZm7fDaoW5LSgGduwCg/+9Z0jqvPJF1Ct1gO0HxVJtvfHPE6LoQVSZrAZV9HvtNQ5O69uxl8YI9cBGlw9TCOlg=="
           :storage-container    "media"}))

(defn upload-phrase-video [{:keys [id movie]}]
  )

(comment

  (azure/list cnt)

  (azure/upload-blob cnt "dir/dir/AnyFile.txt" "README.md")

  )
