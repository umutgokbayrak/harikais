(ns harikais.batch.expire-jobs
  (:require [harikais.data.d-companies :as dc]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-profiles :as dp]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-users :as du]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-messages :as dm]
            [harikais.data.d-favorites :as df]
            [harikais.services.s-offers :as so]
            [harikais.services.s-match :as sm]
            [harikais.services.s-jobs :as sj]
            [harikais.services.s-favorites :as sf]
            [harikais.services.s-messages :as sme]
            [harikais.util :as util]
            [clojure.set :refer [intersection difference]]
            [taoensso.timbre :as timbre]
            [harikais.batch.matchmaker :refer [matchmaker]]
            [environ.core :refer [env]]
            [harikais.services.channels :as chn])
  (:import [java.util Calendar]))


(defn find-expired-jobs [jobs-all]
  (filter
   (fn [job]
     (let [diff-secs (util/secs-until-date (:expire_date job))]
       (and (false? (:is_expired job)) (< diff-secs 0))))
   jobs-all))


(defn expire-jobs
  "Finds jobs that have expired and expires them"
  []
  (let [jobs-all (dj/get-available-jobs)
        jobs (find-expired-jobs jobs-all)]

    (doseq [job jobs]
      (timbre/info "Expiring job with id:" (:job_id job))
      (let [job-id (:job_id job)
            offers (dof/get-offers-by-job-id job-id)
            chats (dm/chats-by-job-id job-id)
            favorites (df/favorites-by-job-id job-id)]
        ; expire the job
        (sj/expire-job-by-id! job-id)

        ; expire the offers for job
        (doseq [offer offers]
          (so/expire-offer-by-id! (:id offer)))

        ; expire the chats for this job
        (doseq [chat chats]
          (sme/expire-chat-by-id! (:chat_id chat)))

        ; delete the favorites
        (doseq [favorite favorites]
          (sf/expire-fav-by-id! (:user_id favorite) (:id favorite)))))))
