(ns harikais.services.s-autocomplete
  (:require [harikais.db.core :refer :all]))


(defn search-industries [s]
  (map #(:industry %)
       (run db-search-industry {:str (str s "%")})))


(defn search-skills [s]
  (map #(:skill %)
       (run db-search-skill {:str (str s "%")})))


(defn get-functionalities []
  (map #(:functionality %)
       (run db-get-functionalities {})))

(defn search-locations [s]
  (map #(:location %)
       (sort-by #(if (true? (:is_boosted %)) 1 0) >
                (run db-search-location {:str (str s "%")}))))


