(ns harikais.services.s-applications
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-messages :as dm]
            [harikais.util :as util]))


(defn- get-applications-by-job-id [job-id]
  (run db-get-applications-by-job-id {:job_id job-id}))


(defn- applications-for-jobs [jobs]
  (map
   (fn [job]
     (hash-map
      (:job_id job)
      (get-applications-by-job-id (:job_id job))))
   jobs))


(defn- applications-jobs [apps]
  (map (fn [app]
         (let [job-id (:job_id app)
               job (dj/get-job-by-id job-id)
               company (dc/get-company-by-id (:company_id job))]
           {:company (:company_name company)
            :companyId (:company_id company)
            :position (:headline job)
            :icon (cond
                   (:is_accepted app) "accepted"
                   (:is_rejected app) "rejected"
                   :else "applied")
            :date (util/date-format-tr (:created_at app))
            :date-obj (.getTime (:created_at app))}))
       apps))


(defn- applications-chats [apps user-id]
  (map (fn [app]
         (if-let [chat (dm/chat-by-user-id-job-id user-id (:job_id app))]
           (do
             (let [job (dj/get-job-by-id (:job_id app))
                   company (dc/get-company-by-id (:company_id job))]
               {:company (:company_name company)
                :companyId (:company_id company)
                :position (:headline job)
                :icon "chat"
                :date (util/date-format-tr (:created_at chat))
                :date-obj (.getTime (:created_at chat))}))))
       apps))


(defn insert-application! [user-id job-id now cover-letter]
  (run db-insert-application!
       {:user_id user-id
        :job_id job-id
        :created_at now
        :cover_letter cover-letter}))


(defn applications-for-company [company-id]
  (let [jobs (dj/get-jobs-by-company company-id)]
    (applications-for-jobs jobs)))


(defn applications [user-id]
  (let [apps (run db-get-applications {:user_id user-id})
        apps-jobs (applications-jobs apps)
        apps-chats (applications-chats apps user-id)]
    (map
     #(dissoc % :date-obj)
     (sort-by
     :date-obj >
     (remove nil?
      (concat apps-jobs apps-chats))))))

