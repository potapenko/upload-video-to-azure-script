(ns playphraseme.queries.phrases
  (:require [mount.core :as mount]
            [monger.collection :as mc]
            [clojure.pprint :refer [pprint]]
            [monger.operators :refer :all]
            [playphraseme.playphraseme.db :refer :all]
            [playphraseme.playphraseme.doc-id :refer :all]
            [playphraseme.playphraseme.util :as util]
            [playphraseme.playphraseme.queries.movies :as movies]
            [taoensso.timbre :as log]))

(def coll "phrases")

(defn- migrate []
  (mc/ensure-index db coll {:have-video 1 :start 1})
  (mc/ensure-index db coll {:start 1}))

(mount/defstate migrations-phrases
  :start (migrate))

(defn get-phrase-by-id
  [^String phrase-id]
  (stringify-id
   (get-doc-by-id coll (str->id phrase-id))))

(defn insert-phrase! [data]
  (stringify-id
   (add-doc coll (objectify-id data))))

(defn delete-phrase! [^String phrase-id]
  (delete-doc-by-id coll (str->id phrase-id)))

(defn update-phrase!
  ([data] (update-phrase! (:id data) (dissoc data :id)))
  ([^String phrase-id data]
   (update-doc-by-id coll (str->id phrase-id) data)))

(defn get-phrases-count []
  (count-docs coll {}))

(defn find-phrases
  ([pred] (find-phrases pred 0 0))
  ([pred limit] (find-phrases pred 0 limit))
  ([pred skip limit]
   (stringify-id
    (find-docs coll {:pred pred :skip skip :limit limit :sort {:start 1}}))))

(defn find-one-phrase [pred]
  (first
   (find-phrases pred 0 1)))

(defn count-phrases [pred]
  (count-docs coll pred))

(defn get-movies-count []
  (->> (aggregate-docs
        coll [{"$match" {}}
              {"$group" {:_id   {:movie "$movie"}}}
              {"$count" "count"}])
       first
       :count))

(defn get-wrong-movies []
  (->> (aggregate-docs
        coll [{$match {}}
              {$group {:_id {:movie "$movie"} :count {$sum 1} :words {$sum {$size "$words"}}}}
              {$match {:count {$lt 200}}}
              {$sort {:count -1}}])
       (map (fn [x]
              {:movie (-> x :_id :movie) :count (:count x) :words (:words x)}))
       (filter (fn [x]
                 (> (* 2.2 (:count x)) (:words x))))))

(defn get-short-phrases []
  (find-phrases {:text {$regex "^.{1,4}$"}}))


(defn get-phrases-by-movie-id [^String movie-id]
  (find-phrases {:movie movie-id}))

(comment

  (future
    (doseq [x (get-wrong-movies)]
      (let [movie (->> x :movie movies/get-movie-by-id)]
        (log/info (util/movie-info movie) "->" (:count x) "words:" (:words x)))))


  (count (get-wrong-movies))

  (->>
   (get-wrong-movies)
   (map :count)
   (reduce +))


  (->> (get-short-phrases)
       (map :text))


 )
