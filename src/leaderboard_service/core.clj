(ns leaderboard_service.core
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [leaderboard_service.http :as http]
            [leaderboard_service.db :as db])
  (:gen-class))

(defn create-service [config-opts]
  "wires up the web service's dependency graph with provided configuration"
  (let [{:keys [spec http-port is-dev db-conn]} config-opts
        is-dev (if (nil? is-dev) (= "true" (env :is-dev)) is-dev)]
    (component/system-map
     :config-opts config-opts
     :db (db/map->DB {:conn db-conn})
     :http (component/using
            (http/map->HTTP {:spec spec
                             :port http-port
                             :is-dev is-dev})
            [:db]))))

(defn -main [& args]
  "entry point for executing outside of a REPL"
  (let [port (if (nil? (env :port)) "8080" (env :port))
        db-conn (env :database-url)
        system (create-service {:spec "leaderboard-service-api.yml"
                                :http-port (read-string port)
                                :is-dev false
                                :db-conn db-conn})]
    (component/start system)))
