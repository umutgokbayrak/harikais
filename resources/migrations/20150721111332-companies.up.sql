CREATE TABLE IF NOT EXISTS companies
(company_id VARCHAR(50) NOT NULL,
 company_name VARCHAR(200) NOT NULL,
 short_description MEDIUMTEXT NOT NULL,
 location VARCHAR(100) NULL,
 avatar_url VARCHAR(400),
 avatar_data LONGBLOB,
 company_photo LONGBLOB,
 email VARCHAR(100) NOT NULL,
 password VARCHAR(50) NOT NULL,
 created_at TIMESTAMP NOT NULL,
 PRIMARY KEY (company_id),
 UNIQUE KEY customer_email_index (email));

--;;

INSERT INTO companies
(company_id, company_name, short_description,
location, avatar_url, avatar_data, company_photo,
 email, password, created_at)
VALUES
('df655f976f7c9d3263815bd981225cd9', 'Trivia Software A.G', 'Trivia Software bla ipsum dolor sit amet, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero. Maecenas nec velit diam, commodo viverra enim.',
 'İstanbul, Anadolu Yakası', 'http://www.harikais.com/img/avatar2.jpg', null, null,
 'company1@pismail.com', '6e8cd7f743f933158ce8e777136cda9c', '2013-07-05 18:19:03'),
('d196a28097115067fefd73d25b0c0be8', 'Cocophony Robotics', 'Cocophony is, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero. Maecenas nec velit diam, commodo viverra enim.',
 'İstanbul, Avrupa Yakası', 'http://www.harikais.com/img/avatar1.jpg', null, null,
 'company2@pismail.com', '6e8cd7f743f933158ce8e777136cda9c', '2013-07-05 18:19:03'),
('e828ae3339b8d80b3902c1564578804e', 'Public Holdings', 'Public Holdings, consectetur adipiscing elit. Sed vel risus mi, sit amet porta sem. Vivamus sed ornare libero. Maecenas nec velit diam, commodo viverra enim.',
 'İstanbul, (Tümü)', null, null, null,
 'company3@pismail.com', '6e8cd7f743f933158ce8e777136cda9c', '2015-07-05 18:19:03');
