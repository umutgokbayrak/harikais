(ns harikais.api.profile
  (:require [harikais.services.s-profile :as sp]
            [harikais.data.d-profiles :as dp]
            [harikais.data.d-users :as du]
            [harikais.util :refer [error success
                                   default-error
                                   default-success]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]
            [harikais.util :as util]))


(defn save-profile
  "Saves the profile (insert/update automatically handled) API"
  [user-id avatar-url fullname location functionality industry headline
                   experiences education skills access-token linkedin-data]
  (let
    [profile {:fullname fullname :location location
              :functionality functionality :industry industry
              :experiences experiences :education education
              :skills skills}]
    (cond
     (false? (b/valid? profile :fullname [v/string [v/min-count 3]]))
     (error 1 "Lütfen adınız ve soyadınızı bizimle paylaşır mısınız?")
     (false? (b/valid? profile :location [v/string [v/min-count 3]]))
     (error 2 "Lütfen nerede yaşadığınızı bizimle paylaşır mısınız?")
     (false? (b/valid? profile :functionality [v/string [v/min-count 3]]))
     (error 3 "Lütfen kendinize çalışmak istediğiniz bir fonksiyon seçer misiniz?")
     (false? (b/valid? profile :industry [v/string [v/min-count 3]]))
     (error 4 "Lütfen kendinize çalışmak istediğiniz bir endüstri seçer misiniz?")
     (false? (b/valid? profile :experiences [[v/min-count 1]]))
     (error 5 "Lütfen mesleki deneyiminizi bizimle paylaşır mısınız?")
     (false? (b/valid? profile :education [[v/min-count 1]]))
     (error 6 "Lütfen geçmiş eğitim bilgilerinizi bizimle paylaşır mısınız?")
     (false? (b/valid? profile :skills [[v/min-count 1]]))
     (error 7 "Lütfen uzman olduğunuzu düşündüğünüz becerilerden en az 3 tane bizimle paylaşır mısınız?")
     :else
     (try
       (sp/save-profile! user-id avatar-url fullname location functionality industry headline
                         experiences education skills access-token linkedin-data)
       (default-success)
       (catch Exception e
         (do
           (.printStackTrace e)
           (timbre/error "Unable to save profile" e)
           (default-error)))))))


(defn save-avatar
  "Saves the avatar for profile API"
  [user-id file]
  (with-open [out (java.io.ByteArrayOutputStream.)]
    (io/copy (io/input-stream (:tempfile file)) out)
    (sp/save-avatar! user-id (.toByteArray out))
    (default-success)))


(defn get-profile
  "Reads the profile API"
  [user-id]
  (let [user (du/get-user-by-id user-id)
        profile (dp/get-profile user-id)
        out-profile {:email (or (:email user) "")
                     :cvComplete (some? profile)
                     :avatarUrl (or (util/avatar-url profile) "")
                     :fullname (or (:fullname profile) "")
                     :location (or (:location profile) "")
                     :functionality (or (:functionality profile) "")
                     :industry (or (:industry profile) "")
                     :headline (or (:headline profile) "")}
        out-experiences (dp/experiences user-id)
        out-education (dp/educations user-id)
        out-skills (dp/skills user-id)]
    (success (assoc out-profile
               :experience out-experiences
               :education out-education
               :skills out-skills))))

