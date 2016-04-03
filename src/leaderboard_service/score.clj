(ns leaderboard_service.score
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]
            [liberator.representation :refer [ring-response]]
            [halresource.resource :as hal]
            [leaderboard_service.util :as util]
            [leaderboard_service.leaderboard :as leaderboard]
            [clj-time.jdbc]))


;; Scores

(defn get-scores
  "Fetches a page containing all scores for a given board_id up to a specified limit.
   Will always return a sequence (even if the query results in 0 scores)."
  [ctx db-conn]
  (let [board_id (:board_id (util/path-from-request ctx))
        sort_ascending (= (get-in ctx [:leaderboard :sorting_order]) "ascending")
        {:keys [limit page unique fromDate toDate]} (util/query-from-request ctx)
        body (db/fetch-scores db-conn {:board_id board_id,
                                       :page page,
                                       :limit limit,
                                       :unique unique,
                                       ;:users forUsers, ;; FIXME: Skipped till swagger1st supports collectionFormat
                                       :fromDate fromDate,
                                       :toDate toDate,
                                       :sort_ascending sort_ascending})]
    {:scores body}))

(defn delete-scores
  "Deletes all scores with a given board_id."
  [ctx db-conn]
  (let [board_id (:board_id(util/path-from-request ctx))
        affected-rows (db/delete-scores db-conn {:board_id board_id})]
    {:num-deleted-scores affected-rows}))

(defn post-score!
  "Post a score to the database."
  [ctx db-conn]
  (let [board_id (:board_id (util/path-from-request ctx))
        {:keys [score username]} (util/body-from-request ctx)
        body (db/insert-score db-conn {:board_id board_id
                                       :score score
                                       :username username})
        id (:id body)
        loc (str (self-href ctx) "/" id)]
    (-> ctx
        (assoc-in [:hal :href] loc)
        (assoc :score body))))

(defn scores [ctx db spec]
  (let [db-conn (:conn db)
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :post :delete :options]
          :available-media-types (get spec "produces")
          :post! #(post-score! % db-conn)
          :post-redirect? false
          :exists? #(if-let [leaderboard (leaderboard/get-leaderboard % db-conn)]
                     {:leaderboard leaderboard})
          :delete! #(delete-scores % db-conn)
          :handle-created #(let [l (get-in % [:hal :href])]
                            (ring-response (:score %) {:headers {"Location" l}}))
          :handle-exception handle-exception
          :handle-options #(describe-resource % spec)
          :handle-ok #((get-scores % db-conn) :scores)
          :handle-not-found {:error "Leaderboard not found."})]
    (handler ctx)))



;; Score

(defn get-score
  "Fetches a score for a given board_id and score_id.
   Returns nil if the score didn't exist."
  [ctx db-conn]
  (let [{:keys [board_id score_id]} (util/path-from-request ctx)
        score (db/fetch-score db-conn {:board_id board_id,
                                       :id score_id})]
    (first score)))

(defn delete-score
  "Deletes a score with a given board_id and score_id."
  [ctx db-conn]
  (let [{:keys [board_id score_id]} (util/path-from-request ctx)
        affected-rows (db/delete-score db-conn {:board_id board_id,
                                                :id score_id})]
    {:num-deleted-scores affected-rows}))

(defn score [ctx db spec]
  (let [db-conn (:conn db)
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :delete :options]
          :available-media-types (get spec "produces")
          :exists? #(if-let [score (get-score % db-conn)]
                     {:score score})
          :delete! #(delete-score % db-conn)
          :handle-ok :score
          :handle-not-found {:error "Score not found"})]
    (handler ctx)))