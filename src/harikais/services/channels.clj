(ns harikais.services.channels
  (:require [clojure.core.async :as async]))


(defonce send-mail (async/chan 10))
(defonce send-push (async/chan 10))

(defn >! [channel data]
  (if (some? data)
    (async/go
     (async/>!
      channel data))))
