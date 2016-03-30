(ns leaderboard_service.score
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]))


(defn scores [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))

(defn score [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource)]
  (handler ctx)))
