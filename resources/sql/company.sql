-- name: db-get-company-by-id
-- retrieve a company by company-id
SELECT * FROM companies
WHERE company_id = :company_id
