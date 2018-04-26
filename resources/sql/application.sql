-- name: db-get-applications
-- retrieve all applications by user-id
SELECT * FROM applications
WHERE user_id = :user_id


-- name: db-insert-application!
-- apply for a new job
INSERT INTO applications
(job_id, user_id, created_at, cover_letter)
VALUES (:job_id, :user_id, :created_at, :cover_letter)


-- name: db-get-applications-by-job-id
-- retrieve all applications by job-id
SELECT * FROM applications
WHERE job_id = :job_id

