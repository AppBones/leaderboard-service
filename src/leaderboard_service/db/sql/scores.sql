-- src/leaderboard_service/db/sql/scores.sql
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
  username   VARCHAR(320)          NOT NULL
);



-- ### DML ###

-- :name delete-score-by-id :! :n
-- :doc Deletes a score with a given id and returns the number of affected rows [0 to 1]
DELETE FROM scores
WHERE id = :id;


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
Gets a page containing all users for a given board_id up to a specified limit.

Examples:

- (fetch-scores db/db {:board_id 1, :page 1, :limit 10})

- (fetch-scores db/db {:board_id 1, :page 1, :limit 10,
                     :unique true, :users ["User1" "User2"],
                     :fromDate "2016-03-18 10:00:00+01", :toDate "2016-03-18 10:00:00+01"})

Supported options:

:unique   -- only one result for each username
:fromDate -- scores from date with format: `yyyy-MM-dd HH:mm:ssZ`
:toDate   -- scores up to and including date with format: `yyyy-MM-dd HH:mm:ssZ`
:users    -- vector of users to get scores from
*/
SELECT scores_2.*
FROM (SELECT
        --~ (if (true? (params :unique)) "DISTINCT ON (username)")
        *
      FROM (SELECT *
            FROM Scores
            WHERE board_id = :board_id
            --~ (if (contains? params :fromDate) "AND score_date >= (:fromDate)::TIMESTAMP")
            --~ (if (contains? params :toDate) "AND score_date <= (:toDate)::TIMESTAMP")
            --~ (if (contains? params :users) "AND username IN (:v*:users)")
            ORDER BY score DESC
           ) scores_1
     ) scores_2
ORDER BY scores_2.score DESC, scores_2.username ASC
OFFSET (:page - 1) * :limit
LIMIT :limit;
