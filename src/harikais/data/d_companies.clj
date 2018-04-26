(ns harikais.data.d-companies
  (:require [harikais.db.core :refer :all]))


(defn get-company-by-id [company-id]
  (first
     (run db-get-company-by-id
          {:company_id company-id})))
