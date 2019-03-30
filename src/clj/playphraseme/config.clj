(ns playphraseme.config
  (:require [cprop.core :refer [load-config]]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]
            [clojure.string :as string]))

(defn start []
  (merge
   (load-config
    :merge
    [(args)
     (source/from-system-props)
     (source/from-env)])))

(defstate env :start (start))

