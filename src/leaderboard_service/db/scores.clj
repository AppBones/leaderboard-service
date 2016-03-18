(ns leaderboard_service.db.scores
  (:require [hugsql.core :as hugsql]
            [leaderboard_service.db :as db]))

(hugsql/def-db-fns "leaderboard_service/db/sql/scores.sql")


(quote
  ; DDL Examples

  (drop-scores-table db/db)
  (create-scores-table db/db)


  ; DML Examples

  (insert-score db/db {:board_id 1,
                       :score    100,
                       :username "User1"})
  (insert-score db/db {:board_id 1,
                       :score    1000,
                       :username "User2"})
  (insert-score db/db {:board_id 1,
                       :score    1337,
                       :username "User3"})
  (delete-score db/db {:id 7})
  (fetch-scores db/db {:board_id 1,
                       :page     1,
                       :limit    10
                       :sorting_asc true})
  (fetch-scores db/db {:board_id 1,
                       :unique   true,
                       :fromDate "2016-03-17 00:00:00+01",
                       :toDate   "2016-12-24 24:00:00+01"
                       :page     1,
                       :limit    10
                       :users    ["User1", "User2", "User3"]
                       :sorting_asc false})
)