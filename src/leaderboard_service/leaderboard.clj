(ns leaderboard_service.leaderboard
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]))


(defn leaderboards [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))

(defn leaderboard [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))
