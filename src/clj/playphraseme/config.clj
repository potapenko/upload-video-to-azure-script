(ns playphraseme.config
  (:require [cprop.core :refer [load-config]]
            [clojure.pprint :refer [pprint]]
            [clojure.java.io :as io]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]
            [clojure.string :as string]))

(def core-main-config (atom {}))

(defn load-server-id! []
  (let [server-id-path (io/file "./resources/server-id.txt")]
    (when-not (.exists server-id-path)
      (spit server-id-path (str "gc-" (java.util.UUID/randomUUID))))
    {:server-id (slurp server-id-path)}))

(defn start []
  (merge
   (load-server-id!)
   (load-config
    :merge
    [(args)
     @core-main-config
     (source/from-system-props)
     (source/from-env)])))

(defstate env :start (start))

