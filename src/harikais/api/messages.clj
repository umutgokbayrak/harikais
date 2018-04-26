(ns harikais.api.messages
  (:require [harikais.services.s-messages :as sm]
            [harikais.data.d-messages :as dm]
            [taoensso.timbre :as timbre]
            [harikais.util :refer [error success success-raw
                                   default-error default-success]]))

(defn unread-msg-count [user-id]
  (success {:count (dm/unread-msg-count user-id)}))


(defn chats [user-id]
  (success-raw (sm/chats-by-user-id user-id)))


(defn chat-log [user-id job-id]
  (let [messages (sm/chat-log user-id job-id)
        chat (dm/chat-by-user-id-job-id user-id job-id)]
    (sm/flag-unread-messages-as-seen! (:chat_id chat) 2)
    (success-raw messages)))


(defn delete-chat [user-id job-id]
  (try
    (sm/delete-chat! user-id job-id)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))


(defn send-message [user-id job-id msg]
  (try
    (sm/send-message user-id job-id msg)
    (default-success)
    (catch Exception e (do (timbre/error e) (default-error)))))
