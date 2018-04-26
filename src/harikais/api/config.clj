(ns harikais.api.config)


(defn config []
  {:body
   {:result {:minVersion 1
             :minVersionMsg "Kullandığınız uygulama sürümü desteklenmemektedir. Uygulamanızı güncellemek için Tamam tuşuna basınız."
             :redirect {:ios "https://itunes.apple.com/app/id945105838"
                        :android "https://play.google.com/store/apps/details?id=com.tazedirekt&hl=tr"}
;;             :announcement "<p><strong>This is the message of the day.</strong></p><p>I feel happy when this message is displayed when I log in to the app</p>"
             :announcement ""
             :linkedin {:enabled true
                        :clientId "77pq4sgmmc5m9p"
                        :clientSecret "ynPJLYi8GBlHHqra"
                        :redirectUrl "http://www.evdekedivar.com"
                        :redirect "http://www.evdekedivar.com"
                        :permissions ["r_basicprofile"]}}}})
