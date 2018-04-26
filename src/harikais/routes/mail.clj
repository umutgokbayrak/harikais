(ns harikais.routes.mail
  (:require [compojure.core :refer [defroutes GET POST]]
            [harikais.services.s-messages :as sm]
            [harikais.api.profile :refer [save-avatar]]))

(defroutes mail-routes
  (POST "/inbound/37eecd67e9ab6c61039f7029b0fdaba6" {body :body}
        (sm/chat-webhook (slurp body))))

