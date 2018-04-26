-- name: db-get-job-by-id
-- retrieve a job by job-id
SELECT * FROM jobs
WHERE job_id = :job_id


-- name: db-get-available-jobs
-- retrieve available jobs
SELECT * FROM jobs


-- name: db-get-jobs-by-company-id
-- retrieve all jobs by company-id
SELECT * FROM jobs
WHERE company_id = :company_id


-- name: db-get-jobs-schools
-- retrieve schools preferences for job
SELECT * FROM jobs_schools
WHERE job_id = :job_id


-- name: db-get-job-fields-of-study
-- retrieve field of study preferences for job
SELECT * FROM jobs_field_of_studies
WHERE job_id = :job_id


-- name: db-get-job-functionalities
-- retrieve functionality preferences for job
SELECT * FROM jobs_functionality
WHERE job_id = :job_id


-- name: db-get-job-titles
-- retrieve title preferences for job
SELECT * FROM jobs_titles
WHERE job_id = :job_id

-- name: db-get-job-skills
-- retrieve skill preferences for job
SELECT * FROM jobs_skills
WHERE job_id = :job_id


-- name: db-expire-job!
-- expires the job
UPDATE jobs
SET is_expired = true
WHERE job_id = :job_id
