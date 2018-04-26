(ns harikais.batch.expire-warn
  (:require [harikais.data.d-companies :as dc]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-profiles :as dp]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-users :as du]
            [harikais.data.d-offers :as dof]
            [harikais.services.s-offers :as so]
            [harikais.services.s-match :as sm]
            [harikais.util :as util]
            [clojure.set :refer [intersection difference]]
            [taoensso.timbre :as timbre]
            [harikais.batch.matchmaker :refer [matchmaker]]
            [environ.core :refer [env]]
            [harikais.services.channels :as chn])
  (:import [java.util Calendar]))


(defn filter-expired-jobs
  "1 gunden az kaldiysa ve henuz expire etmediyse uyar"
  [jobs-all]
  (filter
   (fn [job]
     (let [diff-secs (util/secs-until-date (:expire_date job))]
       (and (<= diff-secs 86400) (> diff-secs 0))))
   jobs-all))


(defn- reduce-jobs-to-offers
  "Finds offers that were favorited but not applied."
  [jobs]
  (reduce
   (fn [offers job]
     (if (nil? job)
       offers
       (let
         [offers-all
          (into
           #{}
           (dof/get-offers-by-job-id (:job_id job)))
          filtered
          (filter
           #(and (:is_favorited %) (not (:is_applied %)))
           offers-all)]
         (conj offers filtered))))
   []
   jobs))


(defn expire-warn
  "Finds out the users who favorited this offer
  but not applied yet. Last 24 hours"
  []
  (let [jobs-all (dj/get-available-jobs)
        jobs (filter-expired-jobs jobs-all)
        offers (flatten
                (filter
                 #(and (some? %) (not (empty? %)))
                 (reduce-jobs-to-offers jobs)))]
    (doseq [offer offers]
      (let [user (du/get-user-by-id (:user_id offer))
            job (dj/get-job-by-id (:job_id offer))
            company (dc/get-company-by-id (:company_id job))
            subject (str "Favori ilanızın süresi dolmak üzere. ("
                         (:headline job)
                         " - "
                         (:company_name company) ")")]
        ; send email reminder
        (chn/>!
         chn/send-mail
         {:meta {:to (:email user)
                 :subject subject
                 :template "expire_warn"
                 :tag "Expiration"}
          :data {:job job
                 :company company}})

        ; send push reminder
        (chn/>!
         chn/send-push
         {:user-ids [(:user_id offer)]
          :msg subject})))))
