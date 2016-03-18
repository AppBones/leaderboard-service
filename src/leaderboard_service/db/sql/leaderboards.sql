-- src/leaderboard_service/db/sql/leaderboards.sql
-- The Leaderboard Service Leaderboards


-- ### DDL ###

-- :name create-score-type
-- :command :execute
-- :doc Create score_type enum with `int`, `lexical`, and `time`
CREATE TYPE score_type AS ENUM ('int', 'lexical', 'time');

-- :name drop-score-type
-- :command :execute
-- :doc Drops score_type
DROP TYPE IF EXISTS score_type;

-- :name create-sorting-order-type
-- :command :execute
-- :doc Create sorting_order enum with `asccending`, and `descending`
CREATE TYPE sorting_order_type AS ENUM ('ascending', 'descending');

-- :name drop-sorting-order-type
-- :command :execute
-- :doc Drops sorting_order_type
DROP TYPE IF EXISTS sorting_order_type;


-- :name drop-leaderboards-table
-- :command :execute
-- :doc Drops leaderboards table
DROP TABLE IF EXISTS Leaderboards;

-- :name create-leaderboards-table
-- :command :execute
-- :doc Create leaderboards table
CREATE TABLE IF NOT EXISTS Leaderboards (
  id              SERIAL PRIMARY KEY                      NOT NULL,
  title           VARCHAR(50)                             NOT NULL,
  description     TEXT DEFAULT NULL,
  app_id          INT                                     NOT NULL,
  score_type      score_type DEFAULT 'int'                NOT NULL,
  sorting_order   sorting_order_type DEFAULT 'descending' NOT NULL,
  min_score       INT DEFAULT -2147483648                 NOT NULL,
  max_score       INT DEFAULT 2147483647                  NOT NULL,
  suffix_singular VARCHAR(50) DEFAULT 'point'             NOT NULL,
  suffix_plural   VARCHAR(50) DEFAULT 'points'            NOT NULL
);



-- ### DML ###

-- :name fetch-leaderboards :?
-- :doc Gets a page containing all leaderboards up to a specified limit.
SELECT *
FROM Leaderboards
OFFSET (:page - 1) * :limit
LIMIT :limit;


-- :name delete-leaderboard :! :n
-- :doc Deletes a leaderboard with a given id and returns the number of affected rows [0 to 1]
DELETE FROM Leaderboards
WHERE id = :id;


-- :name insert-leaderboard :insert
/* :doc
Inserts a leaderboard and returns the inserted record

Examples:

- (insert-leaderboard db/db {:title "Leaderboard 1"
                             :app_id 1})

- (insert-leaderboard db/db {:title "AppBone Leaderboard",
                             :description "The best leaderboard of them all!",
                             :app_id 1,
                             :score_type "int",
                             :sorting_order "ascending",
                             :min_score -1337,
                             :max_score 1337,
                             :suffix_singular "banana",
                             :suffix_plural "bananas"})
*/
INSERT INTO Leaderboards (title, description, app_id, score_type, sorting_order, min_score, max_score, suffix_singular, suffix_plural)
VALUES (
  :title,
  --~ (if (contains? params :description)     ":description," "DEFAULT,")
  :app_id
  --~ (if (contains? params :score_type)      ", (:score_type)::score_type" ", DEFAULT")
  --~ (if (contains? params :sorting_order)   ", (:sorting_order)::sorting_order_type" ", DEFAULT")
  --~ (if (contains? params :min_score)       ", :min_score" ", DEFAULT")
  --~ (if (contains? params :max_score)       ", :max_score" ", DEFAULT")
  --~ (if (contains? params :suffix_singular) ", :suffix_singular" ", DEFAULT")
  --~ (if (contains? params :suffix_plural)   ", :suffix_plural" ", DEFAULT")
);


-- :name update-leaderboard :! :n
/* :doc
Updates a leaderboard with a given id

Examples:

- (update-leaderboard db/db {:id      2,
                             :updates {:title "New leaderboard name"}})

- (update-leaderboard db/db {:id      1,
                             :updates {:title "Brand new leaderboard, almost!",
                                       :description "The best leaderboard of them all!",
                                       :app_id 1,
                                       :score_type "int",
                                       :sorting_order "descending",
                                       :min_score -1337,
                                       :max_score 1337,
                                       :suffix_singular "banana",
                                       :suffix_plural "bananas"}})
*/
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
UPDATE Leaderboards
SET
  /*~
  (string/join ","
             (for [[field _] (:updates params)]
                  (str (identifier-param-quote (name field) options)
                       " = (:v:updates." (name field) ")"
                       (if (= (name field) "score_type") "::score_type")
                       (if (= (name field) "sorting_order") "::sorting_order_type"))))
  ~*/
WHERE id = :id;
