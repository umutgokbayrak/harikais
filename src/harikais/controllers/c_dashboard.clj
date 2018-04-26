(ns harikais.controllers.c-dashboard)


(defn dashboard [company-id]
    {:stats {:unread-msg 1 ;; TODO: implement this
             :new-apps   3
             :total-apps 44
             :approved-apps  4
             :rejected-apps 3}
     :latest_apps [] ;; flatten et, date'e gore sort et take 10 yap
     :jobs []})
