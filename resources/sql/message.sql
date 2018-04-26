-- name: db-unread-msg-count
-- count number of messages not read
SELECT count(chat_id) as msgs
FROM chats_logs
WHERE user_id = :user_id
AND direction = 2
AND is_seen = false


-- name: db-get-chats-by-user-id
-- retrieve chats by user id
SELECT *
FROM chats
WHERE user_id = :user_id


-- name: db-get-chats-by-job-id
-- retrieve chats by job id
SELECT *
FROM chats
WHERE job_id = :job_id


-- name: db-get-chat-by-id
-- retrieve chats by chat id
SELECT *
FROM chats
WHERE chat_id = :chat_id


-- name: db-unread-msg-count-with-chat-id
-- count num of messages unread for chat
SELECT count(chat_id) as msgs
FROM chats_logs
WHERE chat_id = :chat_id
AND direction = 2
AND is_seen = false


-- name: db-last-message-with-direction
-- returns the last message for direction and chat
SELECT *
FROM chats_logs
WHERE chat_id = :chat_id
AND direction = :direction
ORDER BY created_at DESC
LIMIT 1


-- name: db-get-chat-by-user-id-job-id
-- retrieve chat by user id and job-id
SELECT *
FROM chats
WHERE user_id = :user_id
AND job_id = :job_id


-- name: db-get-chat-log-by-id
-- returns all messages ordered by date for chat
SELECT *
FROM chats_logs
WHERE chat_id = :chat_id
ORDER BY created_at ASC

-- name: db-get-unread-messages
-- returns all unread messages
SELECT *
FROM chats_logs
WHERE chat_id = :chat_id
AND is_seen = false


-- name: db-delete-chat-by-id!
-- deletes chat by id
DELETE FROM chats
WHERE chat_id = :chat_id


-- name: db-delete-chat-logs-by-id!
-- deletes all chat logs by chat id
DELETE FROM chats_logs
WHERE chat_id = :chat_id


-- name: db-insert-chat!
-- create new chat session
INSERT INTO chats
(chat_id, user_id, job_id, created_at)
VALUES (:chat_id, :user_id, :job_id, :created_at)


-- name: db-insert-chat-log!
-- create new message in a chat session
INSERT INTO chats_logs
(chat_id, user_id, direction, msg, is_push_sent,
 is_email_sent, is_seen, created_at,
 push_sent_at, email_sent_at)
VALUES (:chat_id, :user_id, :direction, :msg, :is_push_sent,
        :is_email_sent, :is_seen, :created_at,
        :push_sent_at, :email_sent_at)


-- name: db-update-chat-log!
-- updates the chat log
UPDATE chats_logs
SET is_push_sent = :is_push_sent,
is_email_sent = :is_email_sent,
is_seen = :is_seen,
push_sent_at = :push_sent_at,
email_sent_at = :email_sent_at,
seen_at = :seen_at
WHERE id = :id
AND chat_id = :chat_id


-- name: db-expire-chat!
-- updates the chat to expire it
UPDATE chats
SET is_expired = true
WHERE chat_id = :chat_id


