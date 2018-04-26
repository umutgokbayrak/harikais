-- name: db-create-user!
-- creates a new user record
INSERT INTO users
(user_id, email, password, auth_hash, created_at)
VALUES (:user_id, :email, :password, :auth_hash, :created_at)


-- name: db-get-all-users
-- gets all users
SELECT * FROM users


-- name: db-get-user-by-email-password
-- retrieve a user given the email and password.
SELECT * FROM users
WHERE email = :email
AND password = :password


-- name: db-get-user-by-email-user-hash
-- retrieve a user given the email user-id and auth-hash
SELECT * FROM users
WHERE email = :email
AND user_id = :user_id
AND auth_hash = :auth_hash


-- name: db-get-user-by-id
-- retrieve a user by user-id
SELECT * FROM users
WHERE user_id = :user_id


-- name: db-get-user-by-email
-- retrieve a user by email
SELECT * FROM users
WHERE email = :email


-- name: db-save-login!
-- log the user login
INSERT INTO logins_users
(user_id, login_at)
VALUES (:user_id, :login_at)


-- name: db-get-user-settings
-- retrieve a user settings by user-id
SELECT * FROM users_settings
WHERE user_id = :user_id


-- name: db-get-user-settings-locations
-- retrieve a user prefered locations by user-id
SELECT * FROM users_settings_locations
WHERE user_id = :user_id


-- name: db-update-user-settings!
-- updates the settings for user
UPDATE users_settings
SET notifications = :notifications,
    minimum_salary = :minimum_salary
WHERE user_id = :user_id


-- name: db-insert-user-settings!
-- inserts settings for user
INSERT INTO users_settings
(user_id, notifications, minimum_salary)
VALUES (:user_id, :notifications, :minimum_salary)


-- name: db-insert-location-pref!
-- inserts settings for user
INSERT INTO users_settings_locations
(user_id, location)
VALUES (:user_id, :location)


-- name: db-remove-location-pref!
-- inserts settings for user
DELETE FROM users_settings_locations
WHERE user_id = :user_id
AND location = :location
