(ns harikais.util
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as timbre])
  (:import [java.util Calendar]))


(defn error [code msg]
  {:body {:result {:result code :msg msg}}})


(defn default-error []
  (error -1 "Geçici olarak hizmet veremiyoruz. Lütfen daha sonra tekrar deneyiniz."))


(defn success [data]
  {:body {:result (assoc data :result 0)}})

(defn success-raw [data]
  {:body {:result data}})

(defn default-success []
  {:body {:result {:result 0}}})


(defn cal-by-json-date [json-date]
  (let [start-year (:year json-date)
        start-month (:month json-date)
        start-day (:day json-date)
        cal (Calendar/getInstance)
        year (if (not (nil? start-year)) start-year (.get cal Calendar/YEAR))
        month (if (not (nil? start-month)) start-month 0)
        day (if (not (nil? start-day)) start-day 1)]
    (.set cal Calendar/MONTH month)
    (.set cal Calendar/YEAR year)
    (.set cal Calendar/DAY_OF_MONTH day)
    cal))


(defn diff-in-years [cal1 cal2]
  (/ (- (.getTimeInMillis cal1) (.getTimeInMillis cal2)) (* 1000.0 60 60 24 365)))


(defn diff-in-secs [cal1 cal2]
  (/ (- (.getTimeInMillis cal1) (.getTimeInMillis cal2)) (* 1000.0)))


(defn years-in-position [position]
  (let [start-date (cal-by-json-date (:startDate position))
        end-date (if (not (nil? (:endDate position)))
                   (cal-by-json-date (:endDate position))
                   (Calendar/getInstance))]
    (diff-in-years end-date start-date)))

(def tr-months ["Ocak" "Şubat" "Mart" "Nisan" "Mayıs" "Haziran"
      "Temmuz" "Ağustos" "Eylül" "Ekim" "Kasım" "Aralık"])


(defn date-format-tr [date]
  (if (some? date)
    (let [cal (Calendar/getInstance)
          _   (.setTime cal date)]
      (str (.get cal Calendar/DAY_OF_MONTH) " "
           (nth tr-months (.get cal Calendar/MONTH)) " "
           (.get cal Calendar/YEAR)))))


(defn avatar-url [profile]
  (or
   (or (:avatar_url profile)
       (if (some? (:avatar_data profile))
         (str (:url-prefix env) "/avatar?id=" (:user_id profile))))
   "http://www.harikais.com/img/avatar-placeholder.png"))

(defn company-photo-url [company]
  (or (if (some? (:company_photo company))
         (str (:url-prefix env) "/company_photo?id=" (:company_id company)))
      "http://www.harikais.com/img/company-placeholder.png"))


(defn company-avatar-url [company]
  (or
   (or (:avatar_url company)
       (if (some? (:avatar_data company))
         (str (:url-prefix env) "/company_avatar?id=" (:company_id company))))
   "http://www.harikais.com/img/avatar-placeholder.png"))


(defn pretty-since [date]
  (if (some? date)
    (let [cal (Calendar/getInstance)
          now (Calendar/getInstance)]
      (.setTime cal date)
      (let [secs (diff-in-secs now cal)
            mins (Math/round (/ secs 60.0))
            hours (Math/round (/ secs (* 60.0 60)))
            days (Math/round (/ secs (* 60.0 60 24)))
            weeks (Math/round (/ secs (* 60.0 60 24 7)))
            months (Math/round (/ secs (* 60.0 60 24 7 31)))]
        (cond
         (>= months 1) (str months " ay önce")
         (>= weeks 1) (str weeks " hafta önce")
         (>= days 1) (str days " gün önce")
         (>= hours 1) (str hours " saat önce")
         (>= mins 1) (str mins " dakika")
         (>= secs 1) (str secs " saniye")
         :else "az önce")))))


(defn secs-until-date [expire-date]
  (try
    (if (some? expire-date)
      (let [now (java.util.Date.)]
        (let [expire-cal (Calendar/getInstance)
              now-cal (Calendar/getInstance)]
          (.setTime now-cal now)
          (.setTime expire-cal expire-date)
          (diff-in-secs expire-cal now-cal)))
      (Integer/MAX_VALUE))
    (catch Exception e (timbre/error e))))
