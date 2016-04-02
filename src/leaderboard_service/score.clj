(ns leaderboard_service.score
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]
            [liberator.representation :refer [ring-response]]
            [halresource.resource :as hal]
            [leaderboard_service.util :as util]
            [clj-time.jdbc]))


;; Scores

(defn get-scores
  "Fetches a page containing all scores for a given board_id up to a specified limit."
  [ctx db-conn]
  (let [board_id (get (util/path-from-request ctx) :board_id)
        {:keys [limit page unique fromDate toDate]} (util/query-from-request ctx)
        body (db/fetch-scores db-conn {:board_id board_id,
                                       :page page,
                                       :limit limit,
                                       :unique unique,
                                       ;:users forUsers, ;; Skipped till swagger1st support collectionFormat
                                       :fromDate fromDate,
                                       :toDate toDate
                                       })
        loc (str (self-href ctx))]
    (-> ctx
        (assoc-in [:hal :href] loc)
        (assoc :data body))))

(defn post-score!
  "Post a score to the database."
  [ctx db-conn]
  (let [board_id (get (util/path-from-request ctx) :board_id)
        {:keys [score username]} (util/body-from-request ctx)
        body (db/insert-score db-conn {:board_id board_id
                                       :score score
                                       :username username})
        id (get body :id)
        loc (str (self-href ctx) "/" id)]
    (-> ctx
        (assoc-in [:hal :href] loc)
        (assoc :data body))))

;; TODO: Add DELETE
(defn scores [ctx db spec]
  (let [db-conn (get db :conn)
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :post :delete :options]
          :available-media-types (get spec "produces")
          :post! #(post-score! % db-conn)
          :post-redirect? false
          :handle-created #(let [l (get-in % [:hal :href])]
                            (ring-response (:data %) {:headers {"Location" l}}))
          :handle-exception handle-exception
          :handle-options #(describe-resource % spec)
          :handle-ok #(let [ctx (get-scores % db-conn)]
                       (ring-response (:data ctx) {}))
          )]
    (handler ctx)))



;; Score

(defn score [ctx db path]
  (let [spec (get-in ctx [:swagger :context :definition "paths" path])
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :post :delete :options]
          :available-media-types (get spec "produces")
          :exists? #() ;; Check if resource exists (use for Delete, patch and single get)
          )]
    (handler ctx)))