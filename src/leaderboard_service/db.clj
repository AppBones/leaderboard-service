(ns leaderboard_service.db
  (:require [environ.core :refer [env]]))

(def db (env :database-url))