CREATE TABLE IF NOT EXISTS profiles
(user_id VARCHAR(50) NOT NULL,
 avatar_url VARCHAR(400),
 avatar_data LONGBLOB,
 headline VARCHAR(100),
 fullname VARCHAR(100),
 location VARCHAR(100),
 functionality VARCHAR(100),
 industry VARCHAR(100),
 access_token VARCHAR(250),
 linkedin_data LONGTEXT,
 PRIMARY KEY (user_id),
 KEY index_user_id (user_id));

--;;

CREATE TABLE IF NOT EXISTS profiles_skills
(id INTEGER AUTO_INCREMENT,
 user_id VARCHAR(50) NOT NULL,
 skill VARCHAR(100) NOT NULL,
 PRIMARY KEY (id),
 KEY index_user_id (user_id));


--;;

CREATE TABLE IF NOT EXISTS profiles_educations
(id INTEGER AUTO_INCREMENT,
 user_id VARCHAR(50) NOT NULL,
 school VARCHAR(100),
 field_of_study VARCHAR(100),
 start_month INTEGER,
 start_year INTEGER,
 degree VARCHAR(100),
 end_month INTEGER,
 end_year INTEGER,
 PRIMARY KEY (id),
 KEY index_user_id (user_id));


--;;

CREATE TABLE IF NOT EXISTS profiles_experiences
(id INTEGER AUTO_INCREMENT,
 user_id VARCHAR(50) NOT NULL,
 title VARCHAR(100),
 company_name VARCHAR(200),
 start_date_month INTEGER,
 start_date_year INTEGER,
 end_date_month INTEGER,
 end_date_year INTEGER,
 PRIMARY KEY (id),
 KEY index_user_id (user_id));

