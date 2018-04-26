(ns harikais.batch.matchmaker
  (:require [harikais.data.d-companies :as dc]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-users :as du]
            [harikais.services.s-offers :as so]
            [harikais.services.s-match :as sm]
            [harikais.util :as util]
            [taoensso.timbre :as timbre]))


(defn matchmaker
  "Runs every 2 hours to find the best matches between jobs and
  job-seekers and offers positions to them."
  []
  (let [jobs-all (dj/get-available-jobs)
        jobs (filter
              #(> (util/secs-until-date (:expire_date %)) 7200) ; 2 saatten az kaldiysa offer etmenin cok anlami yok
              jobs-all)]
    (timbre/info "There are " (count jobs) " open positions")
    (doseq [job jobs]
      (timbre/info "Searching candidates for job: " (:headline job) " - " (:job_id job))
      (let [job-id (:job_id job)
            user-ids (map #(:user-id %) (sm/find-matches-for-job job-id))
            users (map #(du/get-user-by-id %) user-ids)
            company (dc/get-company-by-id (:company_id job))]
        (timbre/info "Found " (count user-ids) " candidates for job" job-id)
        (so/multiple-offer-job job users company)))))
