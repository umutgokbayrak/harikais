CREATE TABLE IF NOT EXISTS favorites
(id INTEGER AUTO_INCREMENT,
 user_id VARCHAR(50) NOT NULL,
 job_id VARCHAR(50) NOT NULL,
 is_expired BOOLEAN DEFAULT FALSE,
 created_at TIMESTAMP,
 PRIMARY KEY (id)
 UNIQUE KEY userid_jobid_index (user_id, job_id),
 KEY index_user_id (user_id),
 KEY index_job_id (job_id));
