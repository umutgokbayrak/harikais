(ns harikais.api.user
  (:require [harikais.services.s-user :as su]
            [harikais.data.d-users :as du]
            [harikais.services.s-applications :as sa]
            [harikais.util :refer [error success success-raw
                                   default-error default-success]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as timbre]))


(defn create-user
  "Create User API"
  [email password]
  (try
    (let
      [user {:email email :password password}]
      (cond
       (false?
        (b/valid? user :email v/email))
       (error 1 "Lütfen geçerli bir eposta adresi giriniz.")
       (false?
        (b/valid? user :password [[v/min-count 7]]))
       (error 1 "Şifreniz 6 karakterden uzun olmalıdır.")
       :else
       (let [existing-user (du/get-user-by-email email)]
         (if (nil? existing-user)
           (do
             (try
               (su/create-user! email password nil)
               (success (su/login email password))
               (catch Exception e
                 (do
                   (timbre/error "Unable to create user" e)
                   (default-error)))))
           (error 2 "Bu eposta adresi ile kayıtlı bir kullanıcı mevcut")))))
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn login
  "Login API"
  [email password]
  (try
    (if-let [output (su/login email password)]
      (success output)
      (error 1 "Bilgileri kontrol edip tekrar deneyiniz."))
    (catch Exception e
      (do
        (timbre/error "Unable to log user in" e)
        (default-error)))))


(defn auto-login
  "Auto Login API"
  [email user-id auth-hash]
  (try
    (if-let [output (su/auto-login email user-id auth-hash)]
      (success output)
      (error 1 "Bilgileri kontrol edip tekrar deneyiniz."))
    (catch Exception e
      (do
        (timbre/error "Unable to log user in" e)
        (default-error)))))


(defn user-info
  "User Info API. Fetches the most basic user info."
  [user-id]
  (if (some? user-id)
    (success (su/user-info user-id))))


(defn get-settings
  "Similar to the user-info API with less output."
  [user-id]
  (if (some? user-id)
    (if-let [user-info (su/user-info user-id)]
      (success {:notifications (:notifications user-info)
                :salary (:minimumSalary user-info)
                :places (:locations user-info)}))))


(defn applications
  "Retrieves the applications user made and their statuses"
  [user-id]
  (if (some? user-id)
    (let [apps (sa/applications user-id)]
     (success-raw apps))))


(defn update-notification-setting
  "Upserts the notification setting (true/false)"
  [user-id notification]
  (try
    (if (some? user-id)
      (do
        (su/update-notification user-id notification)
        (default-success)))
    (catch Exception e (do (timbre/error e) (default-error)))))

(defn update-salary-setting
  "Upserts the salary setting (true/false)"
  [user-id salary]
  (if (and (some? user-id) (some? salary))
    (do
      (su/update-salary user-id salary)
      (default-success))))


(defn add-location-preference
  "Adds a location to preferences if it not exists already"
  [user-id location]
  (if (and (some? user-id) (some? location))
    (do
      (su/add-location-preference user-id location)
      (default-success))))


(defn remove-location-preference [user-id location]
  "Removes a location from preferences if it exists"
  [user-id location]
  (if (and (some? user-id) (some? location))
    (do
      (su/remove-location-preference user-id location)
      (default-success))))

