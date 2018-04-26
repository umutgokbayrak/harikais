(ns harikais.integrations.parse
  (:require [clj-http.client :as client]
            [clojure.core.async :as async]
            [harikais.services.channels :as chn]
            [taoensso.timbre :as timbre]))


(defn send-push
  "Sends a push notification to multiple users"
  [user-ids msg]
  (client/post
   "https://api.parse.com/1/push"
   {:body (str "{\"channels\": ["
               (str (clojure.string/join
                     ","
                     (map #(str "\"CHN-" % "\"")
                          user-ids)))
               "], \"data\": {\"alert\":\"" msg "\"}}")
    :headers {"X-Parse-Application-Id" "7EqNvrRwIHC2CP34qAgVJTCCmmReT5gnZdZM5zYP"
              "X-Parse-REST-API-Key" "f8LCXQKaAEzbsStXURKlbIWaPS8yj43Gx7uNgCsq"}
    :content-type :json
    :socket-timeout 1000  ;; in milliseconds
    :conn-timeout 1000    ;; in milliseconds
    :accept :json}))


(defn start-loop []
  (timbre/info "Starting parse.com loop.")
  (async/go-loop
   []
   (let [item (async/<! chn/send-push)]
     (if (> (count (:user-ids item)) 0)
       (do
         (timbre/debug "Sending push notification" item)
         (send-push (:user-ids item)
                    (:msg item)))))
   (recur)))
