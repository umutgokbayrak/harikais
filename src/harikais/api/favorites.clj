(ns harikais.api.favorites
  (:require [harikais.services.s-favorites :as sf]
            [harikais.data.d-favorites :as df]
            [taoensso.timbre :as timbre]
            [harikais.util :refer [success-raw default-success default-error]]))


(defn list-favorites [user-id]
  (success-raw (sf/favorites-by-user-id user-id)))


(defn add-favorite [user-id job-id]
  (try
    (sf/add-favorite! user-id job-id)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn delete-fav-by-id [user-id favorite-id]
  (try
    (sf/delete-fav-by-id! user-id favorite-id)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn delete-fav-by-job-id [user-id job-id]
  (try
    (sf/delete-fav-by-job-id! user-id job-id)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))
