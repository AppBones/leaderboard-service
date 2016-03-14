(ns leaderboard_service.leaderboard
  (:require [ring.util.response :as r]
            [leaderboard_service.util :as u]))


; ### Path: "api/v1/leaderboards"

(defn list-leaderboards
  ; Returns all leaderboards within a specified range
  [request]
  (let [{limit :limit, page :page} (u/get-query request)]
    (-> (r/response {:limit limit :page page})
        (r/content-type "application/json"))))

(defn create-leaderboard
  ; Adds a new leaderboard
  [request]
  (let [leaderboard (u/get-body request)]
    (-> (r/response leaderboard)
        (r/status 201)
        (r/content-type "application/json"))))

(defn list-options
  ; Lists all valid HTTP verbs for "/api/v1/leaderboards"
  [request]
  (let [options "GET, POST, OPTIONS"]
    (-> (r/response nil)
        (r/header "Allow" options)
        (r/content-type "application/json"))))


; ### Path: "api/v1/leaderboards/{board_id}"

(defn fetch-leaderboard
  ; Returns a leaderboard with a given ID
  [request]
  (let [{board-id :board_id} (u/get-path request)]
    (-> (r/response {:board-id board-id})
        (r/content-type "application/json"))))

(defn update-leaderboard
  ; Updates a leaderboard with a given ID
  [request]
  (let [board-id (:board_id (u/get-path request))
        leaderboard (u/get-body request)]
    (-> (r/response leaderboard)
        (r/status 204)
        (r/content-type "application/json"))))

(defn delete-leaderboard
  ; Deletes a leaderboard with a given ID
  [request]
  (let [{board-id :board_id} (u/get-path request)]
    (-> (r/response nil)
        (r/status 202)
        (r/content-type "application/json"))))

(defn list-options-board
  ; Lists all valid HTTP verbs for "/api/v1/leaderboards/{board_id}"
  [request]
  (let [options "GET, PATCH, DELETE, OPTIONS"]
    (-> (r/response nil)
        (r/header "Allow" options)
        (r/content-type "application/json"))))
