(ns harikais.services.s-match
  (:require [clj-fuzzy.metrics :as fuzzy]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-users :as du]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-profiles :as dp]
            [harikais.util :as util]
            [clojure.walk :as w]
            [clojure.string :as str])
  (:import [java.util Calendar]))


(defn- similarity [text1 text2]
  (/ (+ (fuzzy/jaro-winkler (str/lower-case text1) (str/lower-case text2))
        (fuzzy/tversky text1 text2 :alpha 0.9 :beta 0.9 :symmetric true))
     2))


(defn- users-not-offered
  "The list of users that this job is not offered before"
  [job-id]
  (let [offered-users (into #{} (map #(:user_id %) (dof/get-offers-by-job-id job-id)))
        all-users (into #{} (map #(:user_id %) (du/get-all-users)))]
  (clojure.set/difference all-users offered-users)))


(defn- location-filter
  "If job and the user declared any location preference than it should match.
  Elsewise we assume that any place is acceptable."
  [job-location user-id profile]
  (if (some? job-location)
    (let [profile-location (:location profile)
          user-locs (du/get-user-settings-locs user-id)]
      ; eger user location setting girdiyse once ona bak
      (if (and (some? user-locs)
               (> (count user-locs) 0))
        (some #(> (similarity
                   job-location
                   %)
                  0.65)
              user-locs)
        ; location setting girmemis bu durumda location'ina bakalim
        (if (some? profile-location)
          (or (> (similarity
                  job-location
                  profile-location)
                 0.7) ; profilinde yazan sehir ile isin alakasini hesapla
              (> (similarity
                  "Turkiye"
                  profile-location)
                 0.8)) ; Sadece Turkiye girmis olanlari da yakalayalim
          true)))
    true))


(defn- salary-filter
  "If a salary limitation is defined by both company and the user
  take it into consideration. Otherwise just skip this filter."
  [job-salary-end user-id]
  (let [user-salary (or (:minimum_salary (du/get-user-settings user-id)) 0)]
    (if (> user-salary 0)
      (>= job-salary-end user-salary)
      true)))


(defn- experience-filter
  "If any experience (in years) is given as limitation, consider it."
  [job-experience user-id]
  (if (and (some? job-experience) (> job-experience 0))
    (let [user-jobs (dp/experiences-raw user-id)
          first-job-cal (first (sort (map #(util/cal-by-json-date (:dateEnter %)) user-jobs)))
          user-experience (if (some? first-job-cal)
                            (util/diff-in-years (Calendar/getInstance) first-job-cal)
                            0)]
      (if (= 0 (count user-jobs)) ; Henuz profil yaratmamislari da uygun goruyoruz,
        true                      ; Cunku onlari da davet edip convert etmelerini isteriz
        (>= user-experience job-experience)))
    true))


(defn- school-filter
  "If a school limit exists, use it. Otherwise omit."
  [job-id user-id educations]
  (let [job-schools (map #(:school %) (dj/get-job-schools job-id))]
    (if (and (some? job-schools)
             (> (count job-schools) 0))
      (let [user-schools (map #(:school %) educations)
            combined (for [x job-schools y user-schools] [x y])]
        (some? (some #(> (similarity (first %) (second %)) 0.65) combined)))
      true)))


(defn- field-of-study-filter
  "If a field of study at school, limit exists, use it. Otherwise omit."
  [job-id user-id educations]
  (let [job-fields (map #(:field_of_study %) (dj/get-job-fields-of-study job-id))]
    (if (and (some? job-fields)
             (> (count job-fields) 0))
      (let [user-fields (map #(:fieldOfStudy %) educations)
            combined (for [x job-fields y user-fields] [x y])]
        (some? (some #(> (similarity (first %) (second %)) 0.65) combined)))
      true)))


(defn- functionality-filter
  "Checks for job functionality filter"
  [job-id functionality]
  (let [job-functs (map #(:functionality %) (dj/get-job-functionalities job-id))]
    (if (and (some? job-functs) (> (count job-functs) 0) (some? functionality))
      (some? (some #(> (similarity functionality %) 0.85) job-functs))
      true)))


(defn- title-score
  "Matches the titles with the user headline"
  [job-id headline]
  (let [job-titles (map #(:title %) (dj/get-job-titles job-id))]
    (if (and (some? job-titles) (> (count job-titles) 0) (some? headline))
      (w/walk #(Math/round (* 10 (similarity % headline))) #(apply + %) job-titles)
      5)))


(defn- skill-score [job-id user-id]
  (let [job-skills (map #(:skill %) (dj/get-job-skills job-id))]
    (if (and (some? job-skills) (> (count job-skills) 0))
      (let [user-skills (dp/skills user-id)
            combined (for [x job-skills y user-skills] [x y])]
      (w/walk #(Math/round (* 20 (similarity (first %) (second %)))) #(apply + %) combined))
      10)))


(defn find-matches-for-job
  "Filters
  --------
  - job location - user location settings'e uyuyor mu? - 0 veya 1 olarak puan ver (girmis ve uyuyorsa 1, yoksa user location'a bak. fuzzy match yap.)
  - job maas araligini oku, user settings'e uyuyor mu? - 0 ile 1 olarak puan ver (girmis ve uyuyorsa 1, girilmemisse de 1)
  - job aranan deneyim suresini oku, user'in toplam yil tecrubesi ile karsilastir - 0 ile 1 olarak puanla (job spec'te de girilmemisse de 1)
  - jobs schools oku, user education ile karsilastir - 0 veya 1
  - job schools bolum oku, field of study ile karsilastir - 1 veya 1
  - job functionality - user'inkisi karsilastir - 0 veya 1

  Fuzzy fields
  ------------
  - job skill'lari - user skill'lari icerisinde ara (exact match) eger yoksa semantic.
  - jobs titles oku, user headline ile karsilastir"
  [job-id]
  (let [job (dj/get-job-by-id job-id)
        user-ids (users-not-offered job-id)
        scores (map
                (fn [user-id]
                  (let [profile (dp/get-profile user-id)
                        educations (dp/educations user-id)]
                    {:user-id user-id
                     :filters [(location-filter (:location job) user-id profile)
                               (salary-filter (:salary_end job) user-id)
                               (experience-filter (:experience job) user-id)
                               (school-filter job-id user-id educations)
                               (field-of-study-filter job-id user-id educations)
                               (functionality-filter job-id (:functionality profile))]
                     :scores {:skill (skill-score job-id user-id)
                              :title (title-score job-id (:headline profile))}}))
                user-ids)]
    (map
     (fn [item]
       {:user-id (:user-id item)
        :score (+ (:skill (:scores item)) (:title (:scores item)))})
     (take
      100
      (sort-by
       (fn [item]
         (+ (:skill (:scores item)) (:title (:scores item))))
       >
       (filter
        (fn [score-map]
          (and
           (every? true? (:filters score-map))
           (> (+ (:skill (:scores score-map)) (:title (:scores score-map))) 0)))
        scores))))))


;; (find-matches-for-job "c4ca4238a0b923820dcc509a6f75849b")
;; (fuzzy/jaro-winkler "ege" "ege üniversitesi")
;; (fuzzy/tanimoto "ege" "ege üniversitesi")
;; (fuzzy/jaro "ege" "ege üniversitesi")
;; (fuzzy/tversky "ege" "ege üniversitesi")
;; (fuzzy/dice "ege" "ege üniversitesi")
;; (fuzzy/hamming "ege" "ege üniversitesi")
;; (fuzzy/levensthein "ege" "ege üniversitesi")
;; (fuzzy/mra-comparison "ege" "ege üniversitesi")
;; (fuzzy/tversky "CMO" "Chief Information Officer" :alpha 0.5 :beta 0.5 :symmetric true)
;; (fuzzy/tversky "CMO" "Chief Information Officer" :alpha 1 :beta 1 :symmetric true)
;; (fuzzy/tversky "CMO" "Chief Information Officer" :alpha 0.75 :beta 0.75 :symmetric true)
;; (fuzzy/jaro-winkler "CMO" "Chief Information Officer")
;; (similarity "CIO" "Chief Information Officer")
