(ns harikais.data.d-favorites
  (:require [harikais.db.core :refer :all]))


(defn get-favorite-by-id [favorite-id]
  (first
   (run db-get-favorite-by-id
        {:id favorite-id})))


(defn favorites-by-job-id [job-id]
   (run db-get-favorites-by-job-id {:job_id job-id}))
