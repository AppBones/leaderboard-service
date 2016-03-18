(ns leaderboard_service.db.leaderboards
    (:require [hugsql.core :as hugsql]
      [leaderboard_service.db :as db]))

(hugsql/def-db-fns "leaderboard_service/db/sql/leaderboards.sql")



; Examples

(quote
  ; DDL Examples

  (drop-leaderboards-table db/db)
  (drop-score-type db/db)
  (drop-sorting-order-type db/db)
  (create-score-type db/db)
  (create-sorting-order-type db/db)
  (create-leaderboards-table db/db)

  ; DML Examples

  (fetch-leaderboards db/db {:page 1, :limit 10})
  (delete-leaderboard db/db {:id 1})
  
  (insert-leaderboard db/db {:title  "Leaderboard 1"
                             :app_id 1})
  (insert-leaderboard db/db {:title           "AppBone Leaderboard",
                             :description     "The best leaderboard of them all!",
                             :app_id          1,
                             :score_type      "int",
                             :sorting_order   "ascending",
                             :min_score       -1337,
                             :max_score       1337,
                             :suffix_singular "banana",
                             :suffix_plural   "bananas"})

  (update-leaderboard db/db {:id      2,
                             :updates {:title "LEADERBOARD NUMBER 2"}})
  (update-leaderboard db/db {:id      1,
                             :updates {:title           "Best Leaderboard Ever",
                                       :description     "The best leaderboard of them all!",
                                       :app_id          1,
                                       :score_type      "int",
                                       :sorting_order   "descending",
                                       :min_score       -1337,
                                       :max_score       1337,
                                       :suffix_singular "banana",
                                       :suffix_plural   "bananas"}})
)