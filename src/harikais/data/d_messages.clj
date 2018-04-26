(ns harikais.data.d-messages
  (:require [harikais.db.core :refer :all]
            [harikais.util :as util]))


(defn unread-msg-count [user-id]
  (:msgs
   (first
    (run
     db-unread-msg-count
     {:user_id user-id}))))


(defn chat-by-user-id-job-id [user-id job-id]
  (first
   (run
    db-get-chat-by-user-id-job-id
    {:user_id user-id :job_id job-id})))


(defn get-chat-by-id [chat-id]
  (first
   (run
    db-get-chat-by-id
    {:chat_id chat-id})))


(defn unread-msg-count-with-chat-id [chat-id]
  (:msgs
   (first
    (run
     db-unread-msg-count-with-chat-id
     {:chat_id chat-id}))))


(defn last-update-with-direction [chat-id direction]
  (:created_at
   (first
    (run
     db-last-message-with-direction
     {:chat_id chat-id :direction direction}))))


(defn chat-messages [chat-id company-name]
  (map
   (fn [log]
     {:direction (:direction log)
      :from (if (= (:direction log) 1) "Siz" company-name)
      :date (util/pretty-since (:created_at log))
      :date-obj (:created_at log)
      :msg (:msg log)})
   (run db-get-chat-log-by-id {:chat_id chat-id})))



(defn chats-by-job-id [job-id]
  (run db-get-chats-by-job-id {:job_id job-id}))
