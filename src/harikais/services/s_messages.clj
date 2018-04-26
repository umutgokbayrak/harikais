(ns harikais.services.s-messages
  (:require [harikais.db.core :refer :all]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-users :as du]
            [harikais.data.d-messages :as dm]
            [harikais.data.d-companies :as dc]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-profiles :as dp]
            [harikais.services.s-offers :as so]
            [harikais.util :as util]
            [harikais.services.channels :as chn]
            [digest :as digest]
            [harikais.integrations.parse :as parse]
            [clojure.data.json :as json]))


(defn chats-by-user-id [user-id]
  (let [chats (run db-get-chats-by-user-id {:user_id user-id})
        filtered (filter
                  (fn [chat]
                    (or (nil? (:is_expired chat))
                        (false? (:is_expired chat))))
                  chats)]
    (map
     (fn [chat]
       (let [job (dj/get-job-by-id (:job_id chat))
             company (dc/get-company-by-id (:company_id job))
             chat-id (:chat_id chat)]
         {:chatId chat-id
          :jobId (:job_id chat)
          :company (:company_name company)
          :companyId (:company_id company)
          :lastUpdate (util/pretty-since
                       (dm/last-update-with-direction chat-id 2))
          :profileImage (util/company-avatar-url company)
          :unreadCount (dm/unread-msg-count-with-chat-id chat-id)}))
     filtered)))


(defn update-chat-log! [chat-id id chat-log-map]

  (println chat-log-map)

  (run
   db-update-chat-log!
   {:push_sent_at (or (:push_sent_at chat-log-map) nil)
    :email_sent_at (or (:email_sent_at chat-log-map) nil)
    :seen_at (or (:seen_at chat-log-map) nil)
    :is_email_sent (or (:is_email_sent chat-log-map) false)
    :is_seen (or (:is_seen chat-log-map) false)
    :is_push_sent (or (:is_push_sent chat-log-map) false)
    :id id
    :chat_id chat-id}))


(defn expire-chat-by-id! [chat-id]
  (run
   db-expire-chat!
   {:chat_id chat-id}))


(defn flag-unread-messages-as-seen!
  "Bir chat'teki okunmamis mesajlarin tumunu okunmus olarak isaretler"
  [chat-id direction]
  (let [messages
        (filter
         #(= (:direction %) direction)
         (run db-get-unread-messages
              {:chat_id chat-id}))
        now (java.util.Date.)]
    (doseq [message messages]
      (update-chat-log!
       chat-id
       (:id message)
       (assoc message
         :is_seen true
         :seen_at now)))))


(defn chat-log [user-id job-id]
  (let [chat-id (:chat_id (dm/chat-by-user-id-job-id user-id job-id))
        job (dj/get-job-by-id job-id)
        company (dc/get-company-by-id (:company_id job))
        profile (dp/get-profile-summary user-id)
        messages (dm/chat-messages chat-id (:company_name company))]
    {:company {:id (or (:company_id company) "")
               :name (or (:company_name company) "")}
     :images {:direction1 (util/avatar-url profile)
              :direction2 (util/company-avatar-url company)}
     :messages messages}))


(defn delete-chat! [user-id job-id]
  (let [chat-id (:chat_id (dm/chat-by-user-id-job-id user-id job-id))]
    (run db-delete-chat-by-id! {:chat_id chat-id})
    (run db-delete-chat-logs-by-id! {:chat_id chat-id})))


(defn- create-chat [user-id job-id]
  (let [chat-id (digest/md5 (str user-id job-id (rand-int 10000000)))
        now (java.util.Date.)]
    (run
     db-insert-chat!
     {:chat_id chat-id
      :user_id user-id
      :job_id job-id
      :created_at now})

    ; mark offer as chatted
    (let [offer (dof/get-offer-by-user-id-job-id user-id job-id)]
      (so/update-offer!
       user-id
       job-id
       (assoc offer
         :is_chatted true
         :chatted_at now)))
    chat-id))


(defn- get-or-create-chat-id [user-id job-id]
  (let [chat-id (:chat_id (dm/chat-by-user-id-job-id user-id job-id))]
    (if (nil? chat-id)
      (create-chat user-id job-id)
      chat-id)))


(defn send-message
  "Send message method from user to company"
  [user-id job-id msg]
  (let [chat-id (get-or-create-chat-id user-id job-id)
        job (dj/get-job-by-id job-id)
        company (dc/get-company-by-id (:company_id job))
        now (java.util.Date.)]
    (run
     db-insert-chat-log!
     {:chat_id chat-id
      :user_id user-id
      :direction 1
      :msg msg
      :is_push_sent false
      :is_email_sent true
      :is_seen false
      :created_at now
      :push_sent_at nil
      :email_sent_at now})

    ;; TODO: development esnasinda mesaja otomatik reply gelecek
    ;; REMOVE THIS
    (run
     db-insert-chat-log!
     {:chat_id chat-id
      :user_id user-id
      :direction 2
      :msg (str "ECHO... " msg)
      :is_push_sent true
      :is_email_sent true
      :is_seen false
      :created_at now
      :push_sent_at now
      :email_sent_at now})
    ;; REMOVE THIS

    ;; Send email
    (chn/>!
     chn/send-mail
     {:meta {:to (:email company)
             :subject (str (:headline job) " pozisyonu için yeni mesaj var (Aday: " user-id ")")
             :template "chat-to-hr"
             :tag "Chat"
             :hash chat-id}
      :data {:headline (:headline job)
             :user user-id
             :msg msg}})))


(defn send-message-from-company
  "Send message from company to user"
  [chat-id msg]
  (let [chat (dm/get-chat-by-id chat-id)
        job (dj/get-job-by-id (:job_id chat))
        company (dc/get-company-by-id (:company_id job))
        user (du/get-user-by-id (:user_id chat))
        now (java.util.Date.)]
    ; Veritabanina mesaji yaziyoruz
    (run
     db-insert-chat-log!
     {:chat_id chat-id
      :user_id (:user_id chat)
      :direction 2
      :msg msg
      :is_push_sent true
      :is_email_sent true
      :is_seen false
      :created_at now
      :push_sent_at now
      :email_sent_at now})

    ; Kullaniciya e-mail atiyoruz
    (chn/>!
     chn/send-mail
     {:meta {:to (:email user)
             :subject (str (:headline job) " pozisyonu için yeni mesaj var")
             :template "chat-to-user"
             :tag "Chat"}
      :data {:company (:company_name company)
             :msg msg}})

    ; Kullaniciya push notification gonderiyoruz
    (chn/>!
     chn/send-push
     {:user-ids [(:user_id chat)]
      :msg (str (:company_name company) ": " msg)})))



(defn- strip-text-reply [body]
  ;; TODO: Turkce e-mail readerlarda strip etmeyebilir. Parse etmek gerekir.
  (:StrippedTextReply body))


(defn chat-webhook
  "inbound mail is used for chat replies from customers"
  [str-body]
  (try
    (let [body (json/read-str str-body :key-fn keyword)
          chat-id (:MailboxHash body)
          msg (strip-text-reply body)]
      (if (and (some? chat-id) (some? msg) (not= (clojure.string/trim msg) ""))
        (send-message-from-company chat-id msg))
      "ok")
    (catch Exception e ("fail"))))
