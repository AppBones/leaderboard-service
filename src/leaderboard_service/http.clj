(ns leaderboard_service.http
  (:require [io.sarnowski.swagger1st.core :as s1st]
            [com.stuartsierra.component :as component]
            [buddy.core.keys :as keys]
            [org.httpkit.server :refer [run-server]]
            [liberator.dev :refer [wrap-trace]]
            [leaderboard_service.util :refer :all]
            [leaderboard_service.middleware :refer :all]))

(defrecord HTTP [server db spec port is-dev pubkey]
  component/Lifecycle

  (start [this]
    (println ";; Starting HTTP component on port" port "...")
    ;; The resolver-fn is the magic where you can pass in whatever you want to
    ;; the API implementing functions.
    ;; For example, the DB dependency of this component. You could also
    ;; restructure the parameters together or define your own mapping scheme.
    (let [resolver-fn (fn [request-definition]
                        (if-let [cljfn (operationId->func request-definition)]
                          (fn [request]
                            (let [spec (get-in request [:swagger :request])]
                              ;; Here we are actually calling our handler
                              (cljfn request db spec)))))

          handler (-> (s1st/context :yaml-cp spec)
                      (s1st/discoverer)
                      (s1st/mapper)
                      (s1st/parser)
                      (s1st/executor :resolver resolver-fn))

          handler (if is-dev
                    (wrap-trace handler :header :ui)
                    (wrap-oauth handler (keys/str->public-key pubkey)))]

      (assoc this :server (run-server handler {:join? false
                                               :port port}))))

  (stop [this]
    ((:server this))
    (assoc this :server nil)))
