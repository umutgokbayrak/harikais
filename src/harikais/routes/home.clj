(ns harikais.routes.home
  (:require [harikais.layout :as layout]
            [harikais.data.d-profiles :as dp]
            [harikais.data.d-companies :as dc]
            [harikais.controllers.c-dashboard :as cd]
            [harikais.batch.offer-generator :as t]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [redirect response content-type]]))

(defn dump-avatar [id]
  (if-let [profile (dp/get-profile-summary id)]
    (if (some? (:avatar_data profile))
      (with-open [bin (java.io.ByteArrayInputStream. (:avatar_data profile))]
        (-> bin
            response
            (content-type "image/jpeg"))))))


(def +dump-avatar (memoize dump-avatar))


(defn dump-company-avatar [id]
  (if-let [company (dc/get-company-by-id id)]
    (if some? (:avatar_data company)
      (with-open [bin (java.io.ByteArrayInputStream. (:avatar_data company))]
        (-> bin
            response
            (content-type "image/jpeg"))))))

(def +dump-company-avatar (memoize dump-company-avatar))


(defn dump-company-photo [id]
  (if-let [company (dc/get-company-by-id id)]
    (if (some? (:company_photo company))
      (with-open [bin (java.io.ByteArrayInputStream. (:company_photo company))]
        (-> bin
            response
            (content-type "image/jpeg"))))))

(def +dump-company-photo (memoize dump-company-photo))


;; TODO: bu bilgiyi session'dan almalisin
(def company-id "df655f976f7c9d3263815bd981225cd9")


(defn dashboard-page []
  (layout/render "web-templates/dashboard.html"
                 (cd/dashboard company-id)))


(defn home-page []
  (layout/render "web-templates/home.html" {}))


(defn candidates-page []
  (layout/render "web-templates/candidates.html" {}))

(defn new-job-page []
  (layout/render "web-templates/job.html" {}))


(defn list-jobs-page []
  (layout/render "web-templates/list_jobs.html" {}))


(defn login-page []
  (layout/render "web-templates/login.html" {}))


(defn signup-page []
  (layout/render "web-templates/signup.html" {}))


(defn referral-signup-page [code]
  (layout/render "web-templates/referral_signup.html" {}))


(defn pass-reset-page [code email]
  (layout/render "web-templates/pass_reset.html" {}))


(defn account-page []
  (layout/render "web-templates/account.html" {}))

(defn prefs-page []
  (layout/render "web-templates/prefs.html" {}))

(defn messages-page []
  (layout/render "web-templates/messages.html" {}))


(defn terms-page []
  (layout/render "web-templates/terms.html" {}))

(defn privacy-page []
  (layout/render "web-templates/privacy.html" {}))

(defn security-page []
  (layout/render "web-templates/security.html" {}))


(defn help-page []
  (layout/render "web-templates/help.html" {}))

(defn logout []
  (redirect "/"))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/avatar" [id] (+dump-avatar id))
  (GET "/company_photo" [id] (+dump-company-photo id))
  (GET "/company_avatar" [id] (+dump-company-avatar id))
  (GET "/dashboard.html" [] (dashboard-page))
  (GET "/candidates.html" [] (candidates-page))
  (GET "/jobs_new.html" [] (new-job-page))
  (GET "/jobs.html" [] (list-jobs-page))
  (GET "/account.html" [] (account-page))
  (GET "/prefs.html" [] (prefs-page))
  (GET "/messages.html" [] (messages-page))
  (GET "/terms.html" [] (terms-page))
  (GET "/privacy.html" [] (privacy-page))
  (GET "/security.html" [] (security-page))
  (GET "/help.html" [] (help-page))
  (GET "/logout" [] (logout))
  (GET "/login" [] (login-page))
  (GET "/signup" [] (signup-page))
  (GET "/referral-signup.html" [code] (referral-signup-page code))
  (GET "/pass_reset.html" [code email] (pass-reset-page code email))
  (GET "/test-data" [id] (t/generate-test-offers)))

