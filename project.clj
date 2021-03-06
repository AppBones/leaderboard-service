(defproject leaderboard_service "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.0.7"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.3.1"]
                 [buddy "0.13.0"]
                 [compojure "1.5.0"]
                 [environ "1.0.2"]
                 [halresource "0.2.0-SNAPSHOT"]
                 [http-kit "2.1.19"]
                 [io.sarnowski/swagger1st "0.21.0"]
                 [liberator "0.14.1"]
                 [ring "1.4.0"]
                 [com.layerware/hugsql "0.4.4"]
                 [org.postgresql/postgresql "9.4.1207"]
                 [clj-time "0.11.0"]]
  :plugins [[lein-environ "1.0.2"]]
  :min-lein-version "2.0.0"
  :profiles {:uberjar {:main leaderboard_service.core
                       :uberjar-name "leaderboard-service.jar"
                       :aot :all}
             :dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [ring/ring-mock "0.3.0"]
                                  [midje "1.8.3"]]
                   :env {:is-dev "true"}}})
