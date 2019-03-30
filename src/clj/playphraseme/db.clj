(ns playphraseme.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as q]
            [monger.operators :refer :all]
            [mount.core :refer [defstate]]
            [mount.core :as mount]
            [playphraseme.config :refer [env]]))

(defstate db*
  :start (-> env :phrases-database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn get-doc [coll pred]
  (mc/find-one-as-map db coll pred))

(defn find-docs
  [coll {:keys [pred sort skip limit]
               :or   {limit 0 skip 0 sort {} pred {}}}]
  (let [sort (if (= sort :reverse) {:_id -1} sort)]
    (q/with-collection db coll
      (q/find pred)
      (q/sort sort)
      (q/skip skip)
      (q/limit limit))))

(defn find-doc
  [coll {:keys [pred sort skip]
               :or   {skip 0 sort {:_id 1} pred {}}
               :as   params}]
  (first
   (find-docs coll params)))

(defn get-docs [coll pred]
  (mc/find-maps db coll pred))

(defn aggregate-docs [coll stages & opts]
  (apply mc/aggregate (concat [db coll stages] opts)))

(defn get-doc-by-id [coll id]
  (mc/find-map-by-id db coll id))

(defn add-doc
  ([coll doc]
   (mc/insert-and-return db coll doc))
  ([coll doc concern]
   (mc/insert-and-return db coll doc concern)))

(defn add-docs [coll docs]
  (mc/insert-batch db coll docs))

(defn update-doc
  ([coll pred doc options]
   (mc/update db coll pred doc options))
  ([coll pred data]
   (mc/update db coll pred {$set data})))

(defn update-doc-by-id [coll id data]
  (mc/update-by-id db coll id {$set data}))

(defn upsert-doc [coll cond data]
  (mc/upsert db coll cond {$set data}))

(defn unset-field [coll cond field]
  (mc/upsert db coll cond {$unset {field 1}}))

(defn delete-docs [coll pred]
  (mc/remove db coll pred))

(defn delete-doc-by-id [coll id]
  (mc/remove-by-id db coll id))

(defn count-docs
  ([coll] (mc/count db coll))
  ([coll pred]
   (mc/count db coll pred)))

(comment
  (mount/start))


