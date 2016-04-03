(ns leaderboard_service.db
  (:require [com.stuartsierra.component :as component]
            [hugsql.core :as hugsql]))

;; TODO: Reload when calling `reset` from `dev` namespace
(hugsql/def-db-fns "leaderboard_service/sql/leaderboards.sql")
(hugsql/def-db-fns "leaderboard_service/sql/scores.sql")

;; Current way to reload in REPL
(comment
  (do
    (ns leaderboard_service.db)
    (hugsql/def-db-fns "leaderboard_service/sql/scores.sql")
    (hugsql/def-db-fns "leaderboard_service/sql/leaderboards.sql")
    (ns dev)
    (reset))
  )

(defrecord DB []
  component/Lifecycle

  (start [component]
    (println ";; Starting DB component ...")
    (let [conn (get component :conn)]
      (create-score-type conn)
      (create-sorting-order-type conn)
      (create-leaderboards-table conn)
      (create-scores-table conn))
    component)

  (stop [component]
    component))