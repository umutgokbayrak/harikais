(ns harikais.data.d-users
  (:require [harikais.db.core :refer :all]))


(defn get-user-settings [user-id]
  (first
   (run db-get-user-settings
        {:user_id user-id})))


(defn get-user-settings-locs [user-id]
  (map #(:location %)
       (run db-get-user-settings-locations
            {:user_id user-id})))


(defn get-user-by-id [user-id]
  (first
   (run db-get-user-by-id
        {:user_id user-id})))


(defn get-user-by-email [email]
  (first
   (run db-get-user-by-email
        {:email email})))


(defn get-all-users []
   (run db-get-all-users {}))

