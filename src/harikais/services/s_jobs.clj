(ns harikais.services.s-jobs
  (:require [harikais.db.core :refer :all]
            [digest :as digest]))


(defn insert-job!
  "Not complete yet..."
  [headline]
  (let [job-id (digest/md5 (str headline (rand-int 10000000)))]
    ;; TODO: bu job'i gercekten de save et

    ;; TODO: jobs_companies, jobs_field_of_studies,
    ;; jobs_functionality, jobs_schools, jobs_skills, jobs_titles save et
    ))


(defn expire-job-by-id! [job-id]
  (run
   db-expire-job!
   {:job_id job-id}))

