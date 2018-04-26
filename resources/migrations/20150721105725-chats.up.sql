CREATE TABLE IF NOT EXISTS chats
(chat_id VARCHAR(50) NOT NULL,
 is_expired BOOLEAN DEFAULT FALSE,
 user_id VARCHAR(50) NOT NULL,
 job_id VARCHAR(50) NOT NULL,
 created_at TIMESTAMP,
 PRIMARY KEY (chat_id),
 KEY index_user_id (user_id),
 KEY index_job_id (job_id),
 KEY index_user_job (user_id, job_id));

--;;

CREATE TABLE IF NOT EXISTS chats_logs
(id BIGINT AUTO_INCREMENT,
 chat_id VARCHAR(50) NOT NULL,
 user_id VARCHAR(50) NOT NULL,
 direction INTEGER DEFAULT 1,
 msg TEXT NOT NULL,
 is_push_sent BOOLEAN DEFAULT FALSE,
 is_email_sent BOOLEAN DEFAULT FALSE,
 is_seen BOOLEAN DEFAULT FALSE,
 created_at TIMESTAMP,
 push_sent_at TIMESTAMP NULL,
 email_sent_at TIMESTAMP NULL,
 seen_at TIMESTAMP NULL,
 PRIMARY KEY (id),
 KEY index_created_at (created_at),
 KEY index_chat_id (chat_id),
 KEY index_chat_logs_user (user_id, direction, is_seen),
 KEY index_chat_logs_chat (chat_id, direction, is_seen),
 KEY index_chat_logs_last_created (chat_id, direction, created_at),
 KEY index_id_chat_id (id, chat_id));
