(ns harikais.services.s-actions
  (:require [harikais.db.core :refer :all]
            [harikais.services.s-offers :as so]
            [harikais.data.d-offers :as dof]
            [digest :as digest]
            [taoensso.timbre :as timbre]
            [harikais.services.channels :as chn]))


(defn insert-pass-reminder! [email reminder-code]
  (run
   db-insert-pass-reminder!
   {:email email
    :reminder_code reminder-code
    :created_at (java.util.Date.)}))


(defn insert-referral! [user-id job-id email referral-code]
  ; Bu job offer'in bu kisiye zaten onerilmis ise exception alir ama sorun yok, devam eder.
  (try
    (let [now (java.util.Date.)]
      ; insert the referral
      (run
       db-insert-referral!
       {:user_id user-id
        :job_id job-id
        :friend_email email
        :referral_id referral-code
        :is_sent true
        :sent_at now
        :created_at now})

      ;; update the offer
      (let [offer (dof/get-offer-by-user-id-job-id user-id job-id)]
        (so/update-offer!
         user-id
         job-id
         (assoc offer
           :is_referred true
           :referred_at now))))
    (catch Exception e
        (timbre/info "Exception at insert referral" e))))


(defn generate-referral-code [user-id job-id email]
  (digest/md5 (str user-id job-id email (rand-int 10000000))))


(defn generate-reminder-code [email]
  (digest/md5 (str email (rand-int 10000000))))

