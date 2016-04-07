(ns leaderboard_service.leaderboard
  (:require [leaderboard_service.db :as db]
            [liberator.core :refer [resource]]
            [leaderboard_service.api :refer :all]
            [liberator.representation :refer [ring-response]]
            [leaderboard_service.representation]
            [halresource.resource :as hal]
            [leaderboard_service.util :as util]
            [clj-time.jdbc]))


;; Helper Functions

(defn get-leaderboards
  "Fetches a page containing all leaderboards for a given app_id up to a specified limit.
   Will always return a sequence (even if the query results in 0 leaderboards)."
  [ctx db-conn]
  (let [{:keys [appId limit page]} (util/query-from-request ctx)
        body (db/fetch-leaderboards db-conn {:app_id appId
                                             :page   page,
                                             :limit  limit})]
    {:leaderboards body}))

(defn post-leaderboard!
  "Post a leaderboard to the database."
  [ctx db-conn]
  (let [{:keys [title description app_id score_type sorting_order min_score max_score]} (:leaderboard (util/body-from-request ctx))
        body (db/insert-leaderboard db-conn {:title         title
                                             :description   description
                                             :app_id        app_id
                                             :score_type    score_type
                                             :sorting_order sorting_order
                                             :min_score     min_score
                                             :max_score     max_score})
        id (:id body)
        loc (str (self-href ctx) id)]
    (-> ctx
        (assoc-in [:hal :href] loc)
        (assoc :leaderboard body))))

(defn get-leaderboard
  "Fetches a leaderboard for a given board_id.
   Returns nil if the leaderboard doesn't exist."
  [ctx db-conn]
  (let [board_id (get (util/path-from-request ctx) :board_id)
        leaderboard (db/fetch-leaderboard db-conn {:id board_id})]
    (first leaderboard)))

(defn delete-leaderboard
  "Deletes a leaderboard with a given board_id."
  [ctx db-conn]
  (let [board_id (:board_id (util/path-from-request ctx))
        affected-rows (db/delete-leaderboard db-conn {:id board_id})]
    {:num-deleted-scores affected-rows}))

(defn patch-leaderboard!
  "Updates/patches a leaderboard."
  [ctx db-conn]
  (let [board_id (:board_id (util/path-from-request ctx))
        {:keys [app_id title description score_type sorting_order min_score max_score]} (:leaderboard (util/body-from-request ctx))
        affected-rows (db/update-leaderboard db-conn {:id      board_id,
                                                      :updates {:title         title,
                                                                :description   description,
                                                                :app_id        app_id,
                                                                :score_type    score_type,
                                                                :sorting_order sorting_order,
                                                                :min_score     min_score,
                                                                :max_score     max_score}})]
    {:num-updated-rows affected-rows}))



;; /leaderboards

(defn leaderboards [ctx db spec]
  (let [db-conn (:conn db)
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :post :options]
          :available-media-types (get spec "produces")
          :post! #(post-leaderboard! % db-conn)
          :post-redirect? false
          ;:exists? ;; TODO: Check if app_id exists (call App Profile service)
          :handle-created #(let [l (get-in % [:hal :href])]
                            (ring-response (:leaderboard %) {:headers {"Location" l}}))
          :handle-exception handle-exception
          :handle-options #(describe-resource % spec)
          :handle-ok #(get-leaderboards % db-conn)
          :handle-not-found {:message "Leaderboard not found."})]
    (handler ctx)))



;; /leaderboards/{board_id}

(defn leaderboard [ctx db spec]
  (let [db-conn (:conn db)
        handler
        (resource
          :initialize-context {:hal (hal/new-resource (self-href {:request ctx}))}
          :allowed-methods [:get :delete :patch :options]
          :available-media-types (get spec "produces")
          :exists? #(if-let [leaderboard (get-leaderboard % db-conn)]
                     {:leaderboard leaderboard})
          :delete! #(delete-leaderboard % db-conn)
          :patch! #(patch-leaderboard! % db-conn)
          :handle-created #(let [l (get-in % [:hal :href])]
                            (ring-response (:leaderboard %) {:headers {"Location" l}}))
          :handle-exception handle-exception
          :handle-options #(describe-resource % spec)
          :handle-ok :leaderboard
          :handle-not-found {:message "Leaderboard not found."})]
    (handler ctx)))
