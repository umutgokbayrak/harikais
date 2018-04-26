(ns harikais.routes.api
  (:require [harikais.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :refer [response]]
            [harikais.api.config :refer [config]]
            [harikais.api.jobs :refer [offers mark-offer-as-seen unread-offer-count
                                       offer-by-job-id]]
            [harikais.api.messages :refer [unread-msg-count chat-log
                                           send-message chats delete-chat]]
            [harikais.api.favorites :refer [add-favorite list-favorites
                                            delete-fav-by-id delete-fav-by-job-id]]
            [harikais.api.actions :refer [apply-to-job refer-a-friend forgot-password]]
            [harikais.api.user :refer [applications user-info update-notification-setting
                                       update-salary-setting add-location-preference
                                       remove-location-preference create-user
                                       login auto-login get-settings]]
            [harikais.api.profile :refer [save-profile get-profile]]
            [harikais.api.autocomplete :refer [guess-location functionalities guess-skill guess-industry]]))

(defroutes api-routes
  (POST "/config" []
        (config))
  (POST "/jobs" [userId]
        (offers userId))
  (POST "/getJobById" [userId jobId]
        (offer-by-job-id userId jobId))
  (POST "/markJobAsSeen" [userId jobId]
        (mark-offer-as-seen userId jobId))
  (POST "/unreadMsgCount" [userId]
        (unread-msg-count userId))
  (POST "/unreadOpportCount" [userId]
        (unread-offer-count userId))
  (POST "/addFavorite" [userId jobId]
        (add-favorite userId jobId))
  (POST "/applyToJob" [userId jobId message]
        (apply-to-job userId jobId message))
  (POST "/referFriend" [userId jobId friend]
        (refer-a-friend userId jobId friend))
  (POST "/chatLog" [userId jobId]
        (chat-log userId jobId))
  (POST "/sendMessage" [userId jobId message]
        (send-message userId jobId message))
  (POST "/chats" [userId]
        (chats userId))
  (POST "/deleteChat" [userId jobId]
        (delete-chat userId jobId))
  (POST "/favorites" [userId]
        (list-favorites userId))
  (POST "/deleteFavorite" [userId favoriteId]
        (delete-fav-by-id userId favoriteId))
  (POST "/removeFavorite" [userId jobId]
        (delete-fav-by-job-id userId jobId))
  (POST "/applications" [userId]
        (applications userId))
  (POST "/info" [userId]
        (user-info userId))
  (POST "/updateNotification" [userId notification]
        (update-notification-setting userId notification))
  (POST "/updateSalary" [userId salary]
        (update-salary-setting userId salary))
  (POST "/addNewLocation" [userId location]
        (add-location-preference userId location))
  (POST "/deleteLocation" [userId location]
        (remove-location-preference userId location))
  (POST "/autocompleteLocation" [str]
        (guess-location str))
  (POST "/createUser" [email password]
        (create-user email password))
  (POST "/login" [email password]
        (login email password))
  (POST "/autoLogin" [email userId authHash]
        (auto-login email userId authHash))
  (POST "/saveCv" [userId avatarUrl fullname location functionality industry headline
                   experience education skills accessToken linkedinData]
        (save-profile userId avatarUrl fullname location functionality industry headline
                   experience education skills accessToken linkedinData))
  (POST "/forgotPassword" [email]
        (forgot-password email))
  (POST "/functionNames" []
        (functionalities))
  (POST "/autocompleteSkills" [str]
        (guess-skill str))
  (POST "/autocompleteIndustries" [str]
        (guess-industry str))
  (POST "/profile" [userId]
        (get-profile userId))
  (POST "/settings" [userId]
        (get-settings userId)))
