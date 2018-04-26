(ns harikais.services.s-user
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-profiles :as dp]
            [harikais.data.d-users :as du]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-jobs :as dj]
            [harikais.services.s-offers :as so]
            [digest :as digest]
            [harikais.services.channels :as chn]
            [harikais.util :as util]))


(defn- save-login!
  "Logs the customer login"
  [user-id]
  (run db-save-login! {:user_id user-id
                       :login_at (java.util.Date.)}))


(defn create-user!
  "Creates a new user"
  [email password ref-code]
  (let [user-id (digest/md5 (str email (rand-int 10000000)))
        password-md5 (digest/md5 password)
        auth-hash (digest/md5 (str email password (rand-int 10000000)))
        now (java.util.Date.)]
    ; create the user account
    (run db-create-user!
         {:user_id user-id
          :email email
          :password password-md5
          :auth_hash auth-hash
          :created_at now})


    ;; bu kisi icin bir recommendation yaratilmis ve o yuzden uye olduysa
    ;; job'i otomatikman ona offer yap. Ayni zamanda referral'daki bilgilerini de guncelle
    (if (some? ref-code)
      (let [referral (first (run db-get-referral {:referral_id ref-code}))]
        (if (some? referral)
          (let [job (dj/get-job-by-id (:job_id referral))
                user (du/get-user-by-id user-id)
                company (dc/get-company-by-id (:company_id job))]
            (so/offer-job user job company)
            (run db-update-referral!
                 (assoc
                   referral
                   :did_sign_up true
                   :sign_up_at now))))))


    ; Send welcome email to the user
    (chn/>!
     chn/send-mail
     {:meta {:to email
             :subject "Harika İş'e hoş geldiniz"
             :template "welcome-user"
             :tag "Welcome"}
      :data {}})))


(defn login
  "Login by email and password"
  [email password]
  (let [password-md5 (digest/md5 password)
        user (first (run db-get-user-by-email-password {:email email :password password-md5}))
        profile (dp/get-profile-summary (:user_id user))]
    (if (some? user)
      (do
        (save-login! (:user_id user))
        {:result 0
         :cvComplete (some? profile)
         :avatarUrl (util/avatar-url profile)
         :fullname (:fullname profile)
         :authHash (:auth_hash user)
         :userId (:user_id user)}))))


(defn auto-login
  "Autologin by email, user-id and auth-hash"
  [email user-id auth-hash]
  (let [user (first
              (run db-get-user-by-email-user-hash
                   {:email email
                    :user_id user-id
                    :auth_hash auth-hash}))
        profile (dp/get-profile-summary (:user_id user))]
    (if (some? user)
      (do
        (save-login! (:user_id user))
        {:result 0
         :cvComplete (some? profile)
         :avatarUrl (util/avatar-url profile)
         :fullname (:fullname profile)}))))


(defn user-info [user-id]
  (let [user (du/get-user-by-id user-id)
        profile (dp/get-profile user-id)
        settings-locs (du/get-user-settings-locs user-id)
        settings (du/get-user-settings user-id)
        notif (:notifications settings)
        salary (:minimum_salary settings)]
    {:fullname (or (:fullname profile) "-")
     :headline (or (:headline profile) "-")
     :pictureUrl (or (util/avatar-url profile) "")
     :notifications notif
     :minimumSalary (if (nil? salary) 0 salary)
     :locations settings-locs}))


(defn update-notification [user-id notification]
  (if-let [user-settings (du/get-user-settings user-id)]
    (run db-update-user-settings!
         (assoc user-settings
           :notifications (Boolean/valueOf notification)))
    (run db-insert-user-settings!
         {:user_id user-id
          :minimum_salary 0
          :notifications (Boolean/valueOf notification)})))


(defn update-salary [user-id salary]
  (if-let [user-settings (du/get-user-settings user-id)]
    (run db-update-user-settings!
         (assoc user-settings :minimum_salary salary))
    (run db-insert-user-settings!
         {:user_id user-id :minimum_salary salary :notifications 1})))


(defn add-location-preference [user-id location]
  (let [locs (map #(.toLowerCase %) (du/get-user-settings-locs user-id))]
    (if (not (contains? (set locs) (.toLowerCase location)))
      (run db-insert-location-pref!
           {:user_id user-id :location location}))))


(defn remove-location-preference [user-id location]
  (run db-remove-location-pref!
       {:user_id user-id :location location}))
