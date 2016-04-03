(ns leaderboard_service.leaderboard
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]
            [liberator.representation :refer [ring-response]]
            [halresource.resource :as hal]
            [leaderboard_service.util :as util]))


(defn leaderboards [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))


(defn get-leaderboard
  "Fetches a leaderboard for a given board_id.
   Returns nil if the leaderboard doesn't exist."
  [ctx db-conn]
  (let [board_id (get (util/path-from-request ctx) :board_id)
        leaderboard (db/fetch-leaderboard db-conn {:id board_id})]
    (first leaderboard)))

(defn leaderboard [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))
