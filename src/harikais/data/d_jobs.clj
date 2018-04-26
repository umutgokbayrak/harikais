(ns harikais.data.d-jobs
  (:require [harikais.db.core :refer :all]))


(defn get-job-by-id [job-id]
  (first
   (run db-get-job-by-id {:job_id job-id})))


(defn get-jobs-by-company [company-id]
  (run db-get-jobs-by-company-id {:company_id company-id}))


(defn get-job-schools [job-id]
  (run db-get-jobs-schools {:job_id job-id}))


(defn get-job-fields-of-study [job-id]
  (run db-get-job-fields-of-study {:job_id job-id}))


(defn get-job-functionalities [job-id]
  (run db-get-job-functionalities {:job_id job-id}))


(defn get-job-titles [job-id]
  (run db-get-job-titles {:job_id job-id}))


(defn get-job-skills [job-id]
  (run db-get-job-skills {:job_id job-id}))

(defn get-available-jobs []
  (run db-get-available-jobs {}))

