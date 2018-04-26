(ns harikais.data.d-offers
  (:require [harikais.db.core :refer :all]))


(defn get-offers-by-user-id [user-id]
  (run db-get-offers-by-user-id {:user_id user-id}))


(defn get-all-offers []
  (run db-get-all-offers {}))


(defn get-offer-by-user-id-job-id [user-id job-id]
  (first
   (run
    db-get-offer-by-user-id-job-id
    {:user_id user-id :job_id job-id})))


(defn get-offers-by-job-id [job-id]
   (run
    db-get-offers-by-job-id
    {:job_id job-id}))


(defn unread-offer-count [user-id]
  (:oc (first (run db-unread-offer-count {:user_id user-id}))))

