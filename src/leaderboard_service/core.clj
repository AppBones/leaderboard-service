(ns leaderboard_service.core
  (:require [io.sarnowski.swagger1st.core :as s1st]))

(def app
  (-> (s1st/context :yaml-cp "leaderboard-service-api.yaml")
      (s1st/discoverer)
      (s1st/mapper)
      (s1st/parser)
      (s1st/executor)))
