-- name: db-get-profile-summary
-- retrieve a profile summary by user id
SELECT avatar_url, avatar_data, headline, fullname
FROM profiles
WHERE user_id = :user_id


-- name: db-get-profile
-- retrieve a profile by user id
SELECT avatar_url, headline, fullname, location, functionality, industry
FROM profiles
WHERE user_id = :user_id


-- name: db-delete-all-experiences!
-- deletes all experience entries for user
DELETE FROM profiles_experiences
WHERE user_id = :user_id


-- name: db-delete-all-education!
-- deletes all education entries for user
DELETE FROM profiles_educations
WHERE user_id = :user_id


-- name: db-delete-all-skills!
-- deletes all skill entries for user
DELETE FROM profiles_skills
WHERE user_id = :user_id


-- name: db-update-profile!
-- updates the profile for user
UPDATE profiles
SET avatar_url = :avatar_url,
    headline = :headline,
    fullname = :fullname,
    location = :location,
    functionality = :functionality,
    industry = :industry,
    access_token = :access_token,
    linkedin_data = :linkedin_data
WHERE user_id = :user_id


-- name: db-insert-profile!
-- inserts the profile for user
INSERT INTO profiles
(user_id, avatar_url, headline, fullname, location, functionality,
 industry, access_token, linkedin_data)
VALUES
(:user_id, :avatar_url, :headline, :fullname, :location, :functionality,
 :industry, :access_token, :linkedin_data)


-- name: db-insert-profile-experience!
-- inserts a profile experience for user. Only 1 for each time.
INSERT INTO profiles_experiences
(user_id, title, company_name, start_date_month,
 start_date_year, end_date_month, end_date_year)
VALUES
(:user_id, :title, :company_name, :start_date_month,
 :start_date_year, :end_date_month, :end_date_year)


-- name: db-insert-profile-education!
-- inserts a profile education for user. Only 1 for each time.
INSERT INTO profiles_educations
(user_id, school, field_of_study, degree, start_month,
 start_year, end_month, end_year)
VALUES
(:user_id, :school, :field_of_study, :degree, :start_month,
 :start_year, :end_month, :end_year)


-- name: db-insert-profile-skill!
-- inserts a profile skill for user. Only 1 for each time.
INSERT INTO profiles_skills
(user_id, skill)
VALUES
(:user_id, :skill)


-- name: db-insert-avatar!
-- inserts the profile for user with an avatar data
INSERT INTO profiles
(user_id, avatar_data)
VALUES
(:user_id, :avatar_data)


-- name: db-update-avatar!
-- updates the avatar for user
UPDATE profiles
SET avatar_data = :avatar_data
WHERE user_id = :user_id


-- name: db-get-profile-skills
-- retrieve profile skills by user id
SELECT *
FROM profiles_skills
WHERE user_id = :user_id


-- name: db-get-profile-educations
-- retrieve profile educations by user id
SELECT *
FROM profiles_educations
WHERE user_id = :user_id


-- name: db-get-profile-experiences
-- retrieve profile educations by user id
SELECT *
FROM profiles_experiences
WHERE user_id = :user_id
