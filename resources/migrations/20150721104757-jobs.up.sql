CREATE TABLE IF NOT EXISTS jobs
(job_id VARCHAR(50) NOT NULL,
 is_expired BOOLEAN DEFAULT FALSE,
 company_id VARCHAR(50) NOT NULL,
 headline VARCHAR(200) NOT NULL,
 location VARCHAR(100) NOT NULL,
 short_description MEDIUMTEXT NOT NULL,
 long_description LONGTEXT NOT NULL,
 salary_begin INTEGER DEFAULT 0,
 salary_end INTEGER DEFAULT 0,
 create_date TIMESTAMP,
 expire_date TIMESTAMP NULL,
 experience INTEGER DEFAULT 0,
 PRIMARY KEY (job_id),
 KEY index_company_id (company_id));

--;;

INSERT INTO jobs
(job_id, company_id, headline, location, short_description,
 long_description, salary_begin, salary_end, create_date,
 expire_date, experience)
VALUES
('c4ca4238a0b923820dcc509a6f75849b', 'df655f976f7c9d3263815bd981225cd9', 'Senior Java Developer',
 'İstanbul, Anadolu Yakası', '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero.</p> <p>Maecenas nec velit diam, commodo viverra enim. Mauris arcu orci, consectetur non porta eu, lobortis eu lorem.</p>',
 '<p>Senior ipsum dolor sit amet, consectetur adipiscing elit.<p><ul><li>Sed vel risus mi,</li> <li>Sit amet porta sem.</li> <li>Vivamus sed ornare libero.</li> <li>Maecenas nec velit diam, </li></ul><p>Phasellus non enim sapien, eu vulputate libero. In vitae justo erat. Mauris luctus pharetra sem quis scelerisque.</p>',
 3000, 5000, '2015-07-05 18:19:03', null, 1),
('c81e728d9d4c2f636f067f89cc14862c', 'd196a28097115067fefd73d25b0c0be8', 'Frontend Developer',
 'İstanbul, Avrupa Yakası', '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero.</p> <p>Maecenas nec velit diam, commodo viverra enim. Mauris arcu orci, consectetur non porta eu, lobortis eu lorem.</p>',
 '<p>Senior ipsum dolor sit amet, consectetur adipiscing elit.<p><ul><li>Sed vel risus mi,</li> <li>Sit amet porta sem.</li> <li>Vivamus sed ornare libero.</li> <li>Maecenas nec velit diam, </li></ul><p>Phasellus non enim sapien, eu vulputate libero. In vitae justo erat. Mauris luctus pharetra sem quis scelerisque.</p>',
 0, 0, '2015-07-20 10:10:00', null, 0),
('eccbc87e4b5ce2fe28308fd9f2a7baf3', 'e828ae3339b8d80b3902c1564578804e', 'Full-stack Developer',
 'İstanbul (Tümü)', '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero.</p> <p>Maecenas nec velit diam, commodo viverra enim. Mauris arcu orci, consectetur non porta eu, lobortis eu lorem.</p>',
 '<p>Senior ipsum dolor sit amet, consectetur adipiscing elit.<p><ul><li>Sed vel risus mi,</li> <li>Sit amet porta sem.</li> <li>Vivamus sed ornare libero.</li> <li>Maecenas nec velit diam, </li></ul><p>Phasellus non enim sapien, eu vulputate libero. In vitae justo erat. Mauris luctus pharetra sem quis scelerisque.</p>',
 0, 0, '2015-07-28 11:00:00', null, 0);

--;;

CREATE TABLE IF NOT EXISTS jobs_skills
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 skill VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));

--;;

CREATE TABLE IF NOT EXISTS jobs_functionality
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 functionality VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));

--;;

CREATE TABLE IF NOT EXISTS jobs_schools
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 school VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));

--;;

CREATE TABLE IF NOT EXISTS jobs_field_of_studies
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 field_of_study VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));


--;;

CREATE TABLE IF NOT EXISTS jobs_titles
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 title VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));


--;;

CREATE TABLE IF NOT EXISTS jobs_companies
(id INTEGER AUTO_INCREMENT,
 job_id VARCHAR(50) NOT NULL,
 company_name VARCHAR(100) NOT NULL,
 excluded BOOLEAN DEFAULT FALSE,
 PRIMARY KEY (id),
 KEY index_job_id (job_id));

