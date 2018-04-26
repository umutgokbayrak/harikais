(ns harikais.services.s-favorites
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-favorites :as df]
            [harikais.data.d-offers :as dof]
            [harikais.services.s-offers :as so]
            [harikais.services.channels :as chn]
            [harikais.util :as util]))


(defn favorites-by-user-id [user-id]
  (map
   (fn [fav]
     (let [job (dj/get-job-by-id (:job_id fav))
           company (dc/get-company-by-id (:company_id job))]
       {:favoriteId (:id fav)
        :jobId (:job_id fav)
        :company (:company_name company)
        :companyId (:company_id company)
        :photoUrl (util/company-photo-url company)
        :position (:headline job)}))
   (run db-get-favorites-by-user-id {:user_id user-id})))


(defn add-favorite! [user-id job-id]
  (let [now (java.util.Date.)
        offer (dof/get-offer-by-user-id-job-id user-id job-id)]
    (run
     db-add-favorite!
     {:user_id user-id
      :job_id job-id
      :created_at now})

    ; mark offer as favorited
    (so/update-offer!
     user-id
     job-id
     (assoc offer
       :is_favorited true
       :favorited_at now))))



(defn delete-fav-by-id! [user-id favorite-id]
  (let [favorite (df/get-favorite-by-id favorite-id)
        offer (dof/get-offer-by-user-id-job-id user-id (:job_id favorite))]
    (so/update-offer!
     user-id
     (:job_id favorite)
     (assoc offer :is_favorited false)))

  (run db-delete-fav-by-id! {:user_id user-id :id favorite-id}))



(defn delete-fav-by-job-id! [user-id job-id]
  (let [offer (dof/get-offer-by-user-id-job-id user-id job-id)]
    (so/update-offer!
     user-id
     job-id
     (assoc offer :is_favorited false)))
  (run db-delete-fav-by-job-id! {:user_id user-id :job_id job-id}))


(defn expire-fav-by-id! [user-id favorite-id]
  (run db-delete-fav-by-id! {:user_id user-id :id favorite-id}))

