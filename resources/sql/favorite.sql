-- name: db-get-favorite-by-id
-- retrieve favorite by id
SELECT *
FROM favorites
WHERE id = :id


-- name: db-get-favorites-by-user-id
-- retrieve favorites by user id
SELECT *
FROM favorites
WHERE user_id = :user_id


-- name: db-get-favorites-by-job-id
-- retrieve favorites by job id
SELECT *
FROM favorites
WHERE job_id = :job_id


-- name: db-add-favorite!
-- add new favorite
INSERT INTO favorites
(user_id, job_id, is_expired, created_at)
VALUES (:user_id, :job_id, false, :created_at)


-- name: db-delete-fav-by-id!
-- deletes fav by id
DELETE FROM favorites
WHERE user_id = :user_id
AND id = :id


-- name: db-delete-fav-by-job-id!
-- deletes fav by id
DELETE FROM favorites
WHERE user_id = :user_id
AND job_id = :job_id
