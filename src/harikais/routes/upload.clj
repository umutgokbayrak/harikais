(ns harikais.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [harikais.api.profile :refer [save-avatar]]))

(defroutes upload-routes
  (POST "/uploadPhoto" [userId file]
        (save-avatar userId file)))


