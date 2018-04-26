(ns harikais.integrations.postmark
  (:use [postmark.core :only (postmark)])
  (:require [selmer.parser :as parser]
            [selmer.filters :as filters]
            [clojure.core.async :as async]
            [harikais.services.channels :as chn]
            [taoensso.timbre :as timbre]))


(def pm (postmark "f80fd886-ad33-4bce-a202-f43996afcbe1" "merhaba@harikais.com"))
(def inbound-mail-prefix "053b0f71df6978174d0ad3d78a4dca76")
(def inbound-mail-domain "inbound.harikais.com")

(parser/set-resource-path!  (clojure.java.io/resource "templates"))


(defn send-mail [to subject template data tag message-hash]
  (try
    (pm
     {:to to
      :subject subject
      :html
      (parser/render-file
       (str "mail-templates/" template ".html")
       data)
      :tag tag
      :reply-to
      (if (some? message-hash)
        (str inbound-mail-prefix "+" message-hash "@" inbound-mail-domain)
        "merhaba@harikais.com")})
    (catch Exception e (timbre/error e))))



(defn start-loop []
  (timbre/info "Starting postmarkapp.com loop.")
  (async/go-loop
   []
   (let [item (async/<! chn/send-mail)]
     (timbre/debug "Sending email" item)
     (send-mail (:to (:meta item))
                (:subject (:meta item))
                (:template (:meta item))
                (:data item)
                (:tag (:meta item))
                (:hash (:meta item))))
   (recur)))

