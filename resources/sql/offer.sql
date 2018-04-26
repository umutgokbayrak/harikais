-- name: db-get-offers-by-user-id
-- retrieve all offers for user id
SELECT *
FROM offers
WHERE user_id = :user_id
ORDER BY created_at DESC


-- name: db-get-all-offers
-- retrieve all offers
SELECT * FROM offers


-- name: db-unread-offer-count
-- count number of messages not read
SELECT count(id) as oc
FROM offers
WHERE user_id = :user_id
AND is_seen = false


-- name: db-get-offer-by-user-id-job-id
-- retrieve the offer for user id and job-id
SELECT * FROM offers
WHERE user_id = :user_id
AND job_id = :job_id


-- name: db-get-offers-by-job-id
-- retrieve the offer for job-id
SELECT * FROM offers
WHERE job_id = :job_id
ORDER BY created_at DESC


-- name: db-insert-offer!
-- add new offer
INSERT INTO offers
(job_id, user_id, created_at)
VALUES (:job_id, :user_id, :created_at)


-- name: db-update-offer!
-- updates the offer
UPDATE offers
SET is_push_sent = :is_push_sent,
is_email_sent = :is_email_sent,
is_seen = :is_seen,
is_chatted = :is_chatted,
is_applied = :is_applied,
is_favorited = :is_favorited,
is_referred = :is_referred,
is_expired = :is_expired,
push_sent_at = :push_sent_at,
email_sent_at = :email_sent_at,
seen_at = :seen_at,
chatted_at = :chatted_at,
applied_at = :applied_at,
favorited_at = :favorited_at,
referred_at = :referred_at
WHERE user_id = :user_id
AND job_id = :job_id


-- name: db-expire-offer!
-- expires the offer
UPDATE offers
SET is_expired = true
WHERE id = :id
