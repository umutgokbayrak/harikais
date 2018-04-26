CREATE TABLE IF NOT EXISTS users
(user_id VARCHAR(50) NOT NULL,
 email VARCHAR(100) NOT NULL,
 password VARCHAR(50) NOT NULL,
 auth_hash VARCHAR(50) NOT NULL,
 created_at TIMESTAMP NOT NULL,
 PRIMARY KEY (user_id),
 UNIQUE KEY user_email_index (email),
 KEY index_email_password (email, password),
 KEY index_auth_login (email, user_id, auth_hash),
 KEY index_email (email));
