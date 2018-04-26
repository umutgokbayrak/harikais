(ns harikais.batch.offer-generator
  (:require [harikais.services.s-offers :as so]
            [harikais.data.d-offers :as dof]
            [harikais.data.d-jobs :as dj]
            [harikais.data.d-users :as du]
            [harikais.data.d-companies :as dc]))


(defn generate-test-offers []
  (let [users (set (map #(:user_id %) (du/get-all-users)))
        offers (set (map #(:user_id %) (dof/get-all-offers)))
        no-offer-user-ids (clojure.set/difference users offers)]
    (doseq [user-id no-offer-user-ids]
      (let [user (du/get-user-by-id user-id)
            job1 (dj/get-job-by-id "c4ca4238a0b923820dcc509a6f75849b")
            job2 (dj/get-job-by-id "c81e728d9d4c2f636f067f89cc14862c")
            job3 (dj/get-job-by-id "eccbc87e4b5ce2fe28308fd9f2a7baf3")
            company1 (dc/get-company-by-id (:company_id job1))
            company2 (dc/get-company-by-id (:company_id job2))
            company3 (dc/get-company-by-id (:company_id job3))]
        (so/offer-job user job1 company1)
        (so/offer-job user job2 company2)
        (so/offer-job user job3 company3)))
    "FINITO"))
