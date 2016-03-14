(ns leaderboard_service.score
  (:require [ring.util.response :as r]
            [leaderboard_service.util :as u]))


; ### Path: "api/v1/leaderboards/{board_id}/items"

(defn fetch-scores
  ; Returns the scores belonging to the leaderboard identified by board-id.
  [request]
  (let [{board-id :board_id} (u/get-path request)
        {unique :unique, limit :limit, page :page} (u/get-query request)]
    (-> (r/response {:board_id board-id
                     :unique unique
                     :limit limit
                     :page page})
        (r/content-type "application/json"))))

(defn create-score
  ; Adds a score to the leaderboard identified by board-id
  [request]
  (let [{board-id :board_id} (u/get-path request)
        score (u/get-body request)]
    (-> (r/response score)
        (r/status 201)
        (r/content-type "application/json"))))

(defn delete-scores
  ; Deletes all scores in the leaderboard identified by board-id
  [request]
  (let [{board-id :board_id} (u/get-path request)]
    (-> (r/response {:board_id board-id})
        (r/status 202)
        (r/content-type "application/json"))))

(defn list-options-scores
  ; Lists all valid HTTP verbs for "api/v1/leaderboards/{board_id}/items"
  [request]
  (let [options "GET, POST, DELETE, OPTIONS"]
    (-> (r/response nil)
        (r/header "Allow" options)
        (r/content-type "application/json"))))


; ### Path: "api/v1/leaderboards/{board_id}/items/{score_id}"

(defn fetch-score
  ; Returns the score correlating to score-id from the leaderboard
  ; identified by board-id
  [request]
  (let [{board-id :board_id score-id :score_id} (u/get-path request)
        score (u/get-body request)]
    (-> (r/response score)
        (r/content-type "application/json"))))

(defn delete-score
  ; Deletes the score correlating to score-id from the leaderboard
  ; identified by board-id.
  [request]
  (let [{board-id :board_id score-id :score_id} (u/get-path request)]
    (-> (r/response nil)
        (r/status 202)
        (r/content-type "application/json"))))

(defn list-options-score
  ; Lists all valid HTTP verbs for "api/v1/leaderboards/{board_id}/items/{score_id}"
  [request]
  (let [options "GET, DELETE, OPTIONS"]
    (-> (r/response nil)
        (r/header "Allow" options)
        (r/content-type "application/json"))))
