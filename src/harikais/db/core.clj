(ns harikais.db.core
  (:require
   [yesql.core :refer [defqueries]]
   [clojure.java.jdbc :as jdbc]
   [environ.core :refer [env]])
  (:import [java.sql PreparedStatement]))

(defonce db-spec (atom nil))

(defn connect! []
  (reset! db-spec {:connection-uri (env :database-url)}))

(if (nil? @db-spec)
  (connect!))

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))


(defn disconnect! [])

(defn run
  "executes a Yesql query using the given database connection and parameter map
  the parameter map defaults to an empty map and the database conection defaults
  to the conn atom"
  ([query-fn] (run query-fn {}))
  ([query-fn query-map] (run query-fn query-map @db-spec))
  ([query-fn query-map db]
   (try
     (query-fn query-map {:connection db})
     (catch Exception e
       (throw (or (.getNextException e) e))))))


(defqueries "sql/message.sql")
(defqueries "sql/action.sql")
(defqueries "sql/application.sql")
(defqueries "sql/autocomplete.sql")
(defqueries "sql/company.sql")
(defqueries "sql/favorite.sql")
(defqueries "sql/job.sql")
(defqueries "sql/offer.sql")
(defqueries "sql/profile.sql")
(defqueries "sql/user.sql")
