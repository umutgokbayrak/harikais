(ns harikais.api.autocomplete
  (:require [harikais.services.s-autocomplete :as sa]
            [harikais.util :refer [success-raw]]))


(defn guess-location [loc]
  (success-raw (take 5 (sa/search-locations loc))))


(defn functionalities []
  (success-raw (sa/get-functionalities)))


(defn guess-skill [s]
  (success-raw (take 5 (sa/search-skills s))))


(defn guess-industry [s]
  (success-raw (take 5 (sa/search-industries s))))
