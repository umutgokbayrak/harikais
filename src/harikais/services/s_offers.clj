(ns harikais.services.s-offers
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-offers :as dof]
            [environ.core :refer [env]]
            [clojure.core.async :as async]
            [harikais.services.channels :as chn]
            [taoensso.timbre :as timbre]))



(defn update-offer! [user-id job-id offer-map]
  (run
   db-update-offer!
   {:push_sent_at (or (:push_sent_at offer-map) nil)
    :email_sent_at (or (:email_sent_at offer-map) nil)
    :referred_at (or (:referred_at offer-map) nil)
    :favorited_at (or (:favorited_at offer-map) nil)
    :chatted_at (or (:chatted_at offer-map) nil)
    :applied_at (or (:applied_at offer-map) nil)
    :seen_at (or (:seen_at offer-map) nil)
    :is_email_sent (or (:is_email_sent offer-map) false)
    :is_applied (or (:is_applied offer-map) false)
    :is_referred (or (:is_referred offer-map) false)
    :is_seen (or (:is_seen offer-map) false)
    :is_push_sent (or (:is_push_sent offer-map) false)
    :is_chatted (or (:is_chatted offer-map) false)
    :is_favorited (or (:is_favorited offer-map) false)
    :is_expired (or (:is_expired offer-map) false)
    :user_id user-id
    :job_id job-id}))


(defn expire-offer-by-id! [offer-id]
  (run
   db-expire-offer!
   {:id offer-id}))


(defn update-offer-as-seen! [user-id job-id]
  (let [offer (dof/get-offer-by-user-id-job-id user-id job-id)]
    (update-offer!
     user-id
     job-id
     (assoc offer
       :is_seen true
       :seen_at (java.util.Date.)))))


(defn- offer-db-and-mail-ops
  [user job-id user-id now company job]

  (run
   db-insert-offer!
   {:job_id job-id
    :user_id user-id
    :created_at now})

  ;; Sending email
  (chn/>!
   chn/send-mail
   {:meta {:to (:email user)
           :subject "Size bir iş teklifimiz var."
           :template "offer"
           :tag "Offer"}
    :data {:company company
           :job job
           :url-prefix (:url-prefix env)}}))


(defn offer-job [user job company]
  (let [user-id (:user_id user)
        job-id (:job_id job)
        offer (dof/get-offer-by-user-id-job-id user-id job-id)
        now (java.util.Date.)]
    (if (nil? offer)
      (do
        (offer-db-and-mail-ops user job-id user-id (java.util.Date.) company job)
        (chn/>!
         chn/send-push
         {:user-ids [user-id]
          :msg "Size bir iş teklifimiz var."})

        ; update offer that push and email is sent
        (update-offer!
         user-id
         job-id
         (assoc offer
           :is_push_sent true
           :is_email_sent true
           :push_sent_at now
           :email_sent_at now))))))


(defn multiple-offer-job [job users company]
  (doseq [user users]
    (let [user-id (:user_id user)
          job-id (:job_id job)
          offer (dof/get-offer-by-user-id-job-id user-id job-id)
          now (java.util.Date.)]
      (if (nil? offer)
        (do
          (offer-db-and-mail-ops user job-id user-id now company job)
          ; update offer that push and email is sent
          (update-offer!
           user-id
           job-id
           (assoc offer
             :is_push_sent true
             :is_email_sent true
             :push_sent_at now
             :email_sent_at now))))))

    (chn/>!
     chn/send-push
     {:user-ids (map #(:user_id %) users)
      :msg "Size bir iş teklifimiz var."}))

