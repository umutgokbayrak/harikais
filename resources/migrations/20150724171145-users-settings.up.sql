CREATE TABLE IF NOT EXISTS users_settings
(user_id VARCHAR(50) NOT NULL,
 notifications BOOLEAN DEFAULT TRUE,
 minimum_salary INTEGER DEFAULT 0,
 PRIMARY KEY (user_id));

--;;

CREATE TABLE IF NOT EXISTS users_settings_locations
(id INTEGER AUTO_INCREMENT,
 user_id VARCHAR(50) NOT NULL,
 location VARCHAR(100) NOT NULL,
 PRIMARY KEY (id),
 KEY index_user_id (user_id));
