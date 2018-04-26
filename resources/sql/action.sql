-- name: db-insert-pass-reminder!
-- creates a new password reminder
INSERT INTO pass_reminders
(email, reminder_code, created_at)
VALUES (:email, :reminder_code, :created_at)


-- name: db-insert-referral!
-- creates a new referral entry
INSERT INTO referrals
(user_id, job_id, friend_email, referral_id, created_at)
VALUES (:user_id, :job_id, :friend_email, :referral_id, :created_at)


-- name: db-get-referral
-- gets a referral by referral_id
SELECT * from referrals
WHERE referral_id = :referral_id


-- name: db-update-referral!
-- updates the offer
UPDATE referrals
SET is_seen = :is_seen,
is_clicked = :is_clicked,
is_sent = :is_sent,
did_sign_up = :did_sign_up,
seen_at = :seen_at,
clicked_at = :clicked_at,
sent_at = :sent_at,
sign_up_at = :sign_up_at
WHERE referral_id = :referral_id
