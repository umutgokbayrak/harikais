(ns harikais.api.jobs
  (:require [harikais.data.d-jobs :as dj]
            [harikais.services.s-offers :as so]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-companies :as dc]
            [taoensso.timbre :as timbre]
            [harikais.util :refer [success success-raw default-error
                                   default-success company-photo-url]]))


(defn offers [user-id]
  (try
    (success-raw
     (map
      (fn [offer]
        (let [job-id (:job_id offer)
              job (dj/get-job-by-id job-id)
              company (dc/get-company-by-id (:company_id job))]
          {:id job-id
           :shortInfo (:short_description job)
           :company {:id (:company_id company)
                     :name (:company_name company)
                     :info (:short_description company)
                     :photoUrl (company-photo-url company)
                     :location (:location company)}
           :job {:position (:headline job)
                 :salaryBegin (:salary_begin job)
                 :salaryEnd (:salary_end job)
                 :info (:long_description job)}
           :flags {:applied (:is_applied offer)
                   :favorited (:is_favorited offer)}}))
      (filter (fn [offer]
                (or (nil? (:is_expired offer))
                    (false? (:is_expired offer))))
              (dof/get-offers-by-user-id user-id))))
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn mark-offer-as-seen [user-id job-id]
  (try
    (so/update-offer-as-seen! user-id job-id)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn unread-offer-count [user-id]
  (success {:count (dof/unread-offer-count user-id)}))


(defn offer-by-job-id [user-id job-id]
  (let [offer (dof/get-offer-by-user-id-job-id user-id job-id)
        job (dj/get-job-by-id job-id)
        company (dc/get-company-by-id (:company_id job))]
    (success-raw
     {:id job-id
      :shortInfo (:short_description job)
      :company {:id (:company_id company)
                :name (:company_name company)
                :info (:short_description company)
                :photoUrl (company-photo-url company)
                :location (:location company)}
      :job {:position (:headline job)
            :salaryBegin (:salary_begin job)
            :salaryEnd (:salary_end job)
            :info (:long_description job)}
      :flags {:applied (:is_applied offer)
              :favorited (:is_favorited offer)}})))
