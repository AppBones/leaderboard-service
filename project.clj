(defproject leaderboard_service "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [io.sarnowski/swagger1st "0.15.0"]
                 [com.layerware/hugsql "0.4.4"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [environ "1.0.0"]]
  :min-lein-version "2.0.0"
  :profiles {:uberjar {:main leaderboard-service.core
                       :uberjar-name "leaderboard-service.jar"
                       :aot :all}}
  :plugins [[lein-ring "0.9.6"]]

  :ring {:handler leaderboard_service.core/app})
