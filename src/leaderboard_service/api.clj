(ns leaderboard_service.api
  (:require [liberator.core :refer [resource]]
            [taoensso.timbre :as log]
            [liberator.representation :refer [ring-response render-map-generic]]
            [leaderboard_service.representation]))

(defn handle-exception
  "Escape hatch for Liberator exceptions. Any logging, printing or recovery for
  exceptions goes here."
  [ctx]
  (let [e (:exception ctx)]
    (log/error e)))

(defn self-href
  "Constructs a default link to the current resource based on the request map."
  [context]
  (let [p (get-in context [:request :server-port])
        port (if-not (or (= 80 p) (= 443 p)) p nil)
        protocol (name (get-in context [:request :scheme]))
        uri (get-in context [:request :uri])]
    (str protocol "://" (get-in context [:request :server-name]) ":" port uri)))

(defn describe-resource
  "Injects the Swagger specification for the given path into the response body."
  [ctx spec]
  (let [rep (get-in ctx [:request :headers "accept"])
        ctx (assoc ctx :representation {:media-type rep})
        path (get-in ctx [:request :uri])
        body (render-map-generic {path spec} ctx)]
    (ring-response {:body body})))
