(ns leaderboard_service.util)

(defn get-query
  [request]
  (get-in request [:parameters :query]))

(defn get-body
  [request]
  (get-in request [:parameters :body]))

(defn get-path
  [request]
  (get-in request [:parameters :path]))
