(ns playphraseme.storage
  (:refer-clojure :exclude [list])
  (:require [clj4azure.storage.blob :refer :all]))

(def cnt (blob-container
          {:storage-account-name "my-account"
           :storage-account-key  "my-account-key"
           :storage-container    "my-container"}))
