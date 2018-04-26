(ns harikais.middleware
  (:require [harikais.session :as session]
            [harikais.layout :refer [*servlet-context*]]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [clojure.java.io :as io]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [clojure.data.json :as json]))

(defn wrap-servlet-context [handler]
  (fn [request]
    (binding [*servlet-context*
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a servlet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath context)
                     (catch IllegalArgumentException _ context)))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (timbre/error t)
        {:status 500
         :headers {"Content-Type" "text/html"}
         :body (-> "templates/error.html" io/resource slurp)}))))

(defn wrap-dev [handler]
  (if (env :dev)
    (-> handler
        reload/wrap-reload
        wrap-error-page
        wrap-exceptions)
    handler))

(defn wrap-csrf [handler]
  (wrap-anti-forgery handler))

(defn wrap-formats [handler]
  (wrap-restful-format handler :formats [:json-kw :transit-json :transit-msgpack]))

(defn wrap-api-auth [handler]
  (fn [request]
    (let [content-type (get (:headers request) "content-type")
          app-id (get (:headers request) "x-parse-application-id")
          api-key (get (:headers request) "x-parse-rest-api-key")]
      (if (= content-type "application/json")
        (if (or (not= app-id "7EqNvrRwIHC2CP34qAgVJTCCmmReT5gnZdZM5zYP")
                (not= api-key "f8LCXQKaAEzbsStXURKlbIWaPS8yj43Gx7uNgCsq"))
          {:body {:error "unauthorized"}}
          (if-let [str-body (slurp (:body request))]
            (try
              (let [json-body (json/read-str str-body :key-fn keyword)]
                (merge-with merge request {:query-params json-body})
                (handler request))
              (catch Exception e ({:body {:error "invalid content body"}})))
            {:body {:error "invalid content body"}}))
        {:body {:error "invalid content type"}}))))


(defn wrap-api-upload-auth [handler]
  (fn [request]
    (let [app-id (get (:headers request) "x-parse-application-id")
          api-key (get (:headers request) "x-parse-rest-api-key")]
      (if (or (not= app-id "7EqNvrRwIHC2CP34qAgVJTCCmmReT5gnZdZM5zYP")
              (not= api-key "f8LCXQKaAEzbsStXURKlbIWaPS8yj43Gx7uNgCsq"))
        {:body {:error "unauthorized"}}
        (handler request)))))


(defn wrap-base [handler]
  (-> handler
      wrap-dev
      (wrap-idle-session-timeout
        {:timeout (* 60 30)
         :timeout-response (redirect "/")})
      wrap-formats
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in  [:session :store] (memory-store session/mem))))
      wrap-servlet-context
      wrap-internal-error))
