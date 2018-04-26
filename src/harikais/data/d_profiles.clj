(ns harikais.data.d-profiles
  (:require [harikais.db.core :refer :all])
  (:import [java.util Calendar]))


(defn get-profile-summary
  "Profile summary by user-id"
  [user-id]
  (first
   (run
    db-get-profile-summary
    {:user_id user-id})))


(defn get-profile
  "Profile by user-id"
  [user-id]
  (first (run db-get-profile {:user_id user-id})))


(defn experiences-raw [user-id]
  (let [cal (Calendar/getInstance)
        now-year (.get cal Calendar/YEAR)
        now-month (.get cal Calendar/MONTH)]
    (map (fn [k]
           {:title (:title k)
            :name (:company_name k)
            :isCurrent (nil? (:end_date_year k))
            :dateEnter {:year (:start_date_year k) :month (:start_date_month k)}
            :dateExit (if (nil? (:end_date_year k))
                        {:year now-year :month now-month}
                        {:year (:end_date_year k)
                         :month (:end_date_month k)})})
         (run db-get-profile-experiences {:user_id user-id}))))


(defn skills [user-id]
  (if-let [skills (run db-get-profile-skills {:user_id user-id})]
    (map #(:skill %) skills)
    []))


(defn experiences [user-id]
  (map (fn [k]
         {:title (:title k)
          :name (:company_name k)
          :isCurrent (nil? (:end_date_year k))
          :dateEnter (str (:start_date_year k) "-" (:start_date_month k))
          :dateExit (if (nil? (:end_date_year k)) "" (str (:end_date_year k) "-" (:end_date_month k)))})
       (run db-get-profile-experiences {:user_id user-id})))


(defn educations [user-id]
  (map (fn [k]
         {:school (:school k)
          :fieldOfStudy (:field_of_study k)
          :degree (:degree k)
          :isCurrent (nil? (:end_year k))
          :dateEnter (str (:start_year k) "-" (:start_month k))
          :dateExit (if (nil? (:end_year k)) "" (str (:end_year k) "-" (:end_month k)))})
       (run db-get-profile-educations {:user_id user-id})))

