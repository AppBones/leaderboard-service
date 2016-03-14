(defproject leaderboard_service "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [io.sarnowski/swagger1st "0.15.0"]]

  :plugins [[lein-ring "0.9.6"]]

  :ring {:handler leaderboard_service.core/app})
