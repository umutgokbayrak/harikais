(ns harikais.api.actions
  (:require [harikais.services.s-actions :as sa]
            [harikais.services.s-applications :as sap]
            [harikais.data.d-users :as du]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-profiles :as dp]
            [harikais.services.s-offers :as so]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-companies :as dc]
            [harikais.services.s-offers :as so]
            [harikais.services.channels :as chn]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [harikais.util :refer [error success-raw default-success default-error]]))


(defn- send-invitation [user-id job-id email]
  (let [referral-code (sa/generate-referral-code user-id job-id email)
        profile (dp/get-profile-summary user-id)
        job (dj/get-job-by-id job-id)
        company (dc/get-company-by-id (:company_id job))]
    (sa/insert-referral! user-id job-id email referral-code)

    ;; Send email
    (chn/>!
     chn/send-mail
     {:meta {:to email
             :subject "Arkadaşınız size yeni bir iş fırsatı önerdi"
             :template "referral"
             :tag "Referral"}
      :data {:friend (:fullname profile)
        :company (:company_name company)
        :headline (:headline job)
        :location (:location job)
        :link (str (:url-prefix env) "/referral-signup.html?code=" referral-code)}})))


(defn- send-pass-reminder [email reminder-code]
  (chn/>!
   chn/send-mail
   {:meta {:to email
           :subject "Şifre Sıfırlama Talimatları"
           :template "password-forget"
           :tag "Password"}
    :data {:link
           (str
            (:url-prefix env)
            "/pass_reset.html?code="
            reminder-code
            "&email="
            (java.net.URLEncoder/encode email))}}))


(defn apply-to-job [user-id job-id cover-letter]
  (try
    (let [job (dj/get-job-by-id job-id)
          offer (dof/get-offer-by-user-id-job-id user-id job-id)
          now (java.util.Date.)]
      (so/update-offer!
       user-id
       job-id
       (assoc offer
         :is_applied true
         :applied_at now))
      (sap/insert-application! user-id job-id now cover-letter))
    (catch Exception e (timbre/error e)))

  ;; TODO: insan kaynaklarina yeni application geldigi icin email at

  (default-success))


(defn refer-a-friend [user-id job-id friend-email]
  (try
    (let [user (du/get-user-by-email friend-email)
          job (dj/get-job-by-id job-id)
          customer (dc/get-company-by-id (:company_id job))]
      (if (some? user)
        (so/offer-job user job customer)
        (send-invitation user-id job-id friend-email))
      (default-success))
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn forgot-password [email]
  (try
    (if-let [user (du/get-user-by-email email)]
      (let [reminder-code (sa/generate-reminder-code email)]
        (sa/insert-pass-reminder! email reminder-code)
        (send-pass-reminder email reminder-code)
        (default-success))
      (error 2 "Lütfen eposta adresinizi kontrol ediniz."))
    (catch Exception e (do (timbre/error e) (default-error)))))
