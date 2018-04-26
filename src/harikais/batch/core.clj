(ns harikais.batch.core
  (:use [schejulure.core])
  (:require [taoensso.timbre :as timbre]
            [harikais.batch.matchmaker :refer [matchmaker]]
            [harikais.batch.expire-warn :refer [expire-warn]]))


(defn start-batches []
  (timbre/info "Initializing batches...")
  (schedule {:hour (range 0 24 2) :minute 0} matchmaker)
  (schedule {:hour 9 :minute 30} expire-warn))
