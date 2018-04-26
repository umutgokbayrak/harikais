(ns harikais.services.s-profile
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-profiles :as dp]
            [clojure.string :as str]
            [clojure.walk :as walk]))


(defn- delete-all-experiences! [user-id]
  (run db-delete-all-experiences! {:user_id user-id}))


(defn- delete-all-education! [user-id]
  (run db-delete-all-education! {:user_id user-id}))


(defn- delete-all-skills! [user-id]
  (run db-delete-all-skills! {:user_id user-id}))


(defn- save-experiences! [user-id json-experiences]
  (let [experiences (map #(walk/keywordize-keys %) json-experiences)]
    (doseq [experience experiences]
      (let [split-start (str/split
                         (:dateEnter experience) #"-")
            split-end (if (some? (:dateExit experience))
                        (str/split (:dateExit experience) #"-"))]
        (run db-insert-profile-experience!
             {:user_id user-id
              :title (:title experience)
              :company_name (:name experience)
              :start_date_month (second split-start)
              :start_date_year (first split-start)
              :end_date_month (if (not (:isCurrent experience))
                                (second split-end))
              :end_date_year (if (not (:isCurrent experience))
                               (first split-end))})))))


(defn- save-education! [user-id json-education]
  (let [educations (map #(walk/keywordize-keys %) json-education)]
    (doseq [edu educations]
      (let [split-start (str/split
                         (:dateEnter edu) #"-")
            split-end (if (some? (:dateExit edu))
                        (str/split (:dateExit edu) #"-"))]
        (run db-insert-profile-education!
             {:user_id user-id
              :school (:school edu)
              :degree (:degree edu)
              :field_of_study (:fieldOfStudy edu)
              :start_month (second split-start)
              :start_year (first split-start)
              :end_month (if (not (:isCurrent edu))
                           (second split-end))
              :end_year (if (not (:isCurrent edu))
                          (first split-end))})))))


(defn- save-skills! [user-id skills]
  (doseq [skill skills]
    (run db-insert-profile-skill! {:user_id user-id :skill skill})))


(defn save-profile!
  [user-id avatar-url fullname location functionality
   industry headline experiences education skills
   access-token linkedin-data]

  ; it is easier to have a fresh start
  (delete-all-experiences! user-id)
  (delete-all-education! user-id)
  (delete-all-skills! user-id)

  ; now save the experiences, education and skills
  (save-experiences! user-id experiences)
  (save-education! user-id education)
  (save-skills! user-id skills)

  ; now upsert the profile
  (let [profile-map
        {:user_id user-id
         :avatar_url avatar-url
         :headline headline
         :fullname fullname
         :location location
         :functionality functionality
         :industry industry
         :access_token access-token
         :linkedin_data (str linkedin-data)}]
    ; check if profile exists or not
    (if-let [profile (dp/get-profile user-id)]
      (run db-update-profile! profile-map)
      (run db-insert-profile! profile-map))))


(defn save-avatar! [user-id avatar-data]
  (let [profile-map {:user_id user-id :avatar_data avatar-data}]
    (if-let [profile (dp/get-profile user-id)]
      (run db-update-avatar! profile-map)
      (run db-insert-avatar! profile-map))))

