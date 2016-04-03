-- src/leaderboard_service/sql/scores.sql
-- The Leaderboard Service Scores


-- ### DDL ###

-- :name drop-scores-table
-- :command :execute
-- :doc Drop scores table
DROP TABLE IF EXISTS Scores;


-- :name create-scores-table
-- :command :execute
-- :doc Create scores table
CREATE TABLE IF NOT EXISTS Scores (
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  board_id   INT                   NOT NULL,
  score      INT                   NOT NULL,
  score_date TIMESTAMP             NOT NULL DEFAULT current_timestamp,
  username   VARCHAR(320)          NOT NULL,
  FOREIGN KEY (board_id) REFERENCES Leaderboards (id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- ### DML ###

-- :name delete-score :! :n
-- :doc Deletes a single score with a given id and returns the number of deleted rows [0 to 1]
DELETE FROM Scores
WHERE id = :id AND board_id = :board_id;


-- :name delete-scores :! :n
-- :doc Deletes all scores for a given board_id and returns the number of deleted rows
DELETE FROM Scores
WHERE board_id = :board_id;


-- :name fetch-score :?
-- :doc Returns a single score given a board_id and the id of the score
SELECT *
FROM Scores
WHERE id = (:id)::BIGINT AND
      board_id = :board_id;


-- :name insert-score :insert
/* :doc
Inserts a score and returns the inserted record

Examples:

(insert-score db/db {:board_id   1,
                     :score      1337,
                     :username   "User"})

(insert-score db/db {:board_id   1,
                     :score      1337,
                     :score_date "2016-03-18 24:00:00+01",
                     :username   "User"})
*/
INSERT INTO Scores (board_id, score, score_date, username)
VALUES (:board_id,
        :score,
        --~ (if (contains? params :score_date) "(:score_date)::TIMESTAMP," "DEFAULT,")
        :username);


-- :name fetch-scores :?
/* :doc
Gets a page containing all scores for a given board_id up to a specified limit.

Examples:

- (fetch-scores db/db {:board_id 1,
                       :page 1,
                       :limit 10})

- (fetch-scores db/db {:board_id 1,
                       :page 1,
                       :limit 10,
                       :unique true,
                       :users ["User1" "User2"],
                       :fromDate "2016-03-18 10:00:00+01",
                       :toDate "2016-03-18 10:00:00+01"
                       :sort_ascending true})

Supported options:

:unique         -- only one result for each username
:fromDate       -- scores from date with format: `yyyy-MM-dd HH:mm:ssZ`
:toDate         -- scores up to and including date with format: `yyyy-MM-dd HH:mm:ssZ`
:users          -- vector of users to get scores from
:sort_ascending -- if true, sort scores ascending, else descending
*/
SELECT s.*
FROM
  -- Create bestScores table, only on unique=true
  /*~ (if (true? (params :unique))  (if (true? (params :sort_ascending)) */
  (SELECT
    board_id,
    username,
    min(score) AS score
  FROM Scores
  GROUP BY username, board_id) bestScores,
  /*~*/
  (SELECT
     board_id,
     username,
     max(score) AS score
   FROM Scores
   GROUP BY username, board_id) bestScores,
  /*~ )) ~*/
  Scores s
WHERE
  s.board_id = :board_id
  --~ (if (not= (get params :fromDate) nil) "AND s.score_date >= (:fromDate)::TIMESTAMP")
  --~ (if (not= (get params :toDate) nil) "AND s.score_date <= (:toDate)::TIMESTAMP")
  --~ (if (seq (:users params)) "AND s.username IN (:v*:users)")
  -- Join s and bestScores, only on unique=true
  /*~ (if (true? (params :unique)) */
  AND s.board_id = bestScores.board_id
  AND s.username = bestScores.username
  AND s.score = bestScores.score
  /*~ ) ~*/
ORDER BY
  --~ (if (true? (params :sort_ascending)) "s.score ASC ," "s.score DESC ," )
  s.username ASC
OFFSET (:page - 1) * :limit
LIMIT :limit;


