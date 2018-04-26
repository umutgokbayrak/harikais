-- name: db-search-industry
-- run a full text search on industries
SELECT *
FROM industries
WHERE industry COLLATE UTF8_GENERAL_CI LIKE :str


-- name: db-search-skill
-- run a full text search on skills
SELECT *
FROM skills
WHERE skill COLLATE UTF8_GENERAL_CI LIKE :str


-- name: db-search-location
-- run a full text search on locations
SELECT *
FROM locations
WHERE location COLLATE UTF8_GENERAL_CI LIKE :str


-- name: db-get-functionalities
-- get all functionalities
SELECT *
FROM functionalities



