(ns leaderboard_service.api
  (:require [ring.util.response :as r]
            [liberator.core :refer [resource]]
            [halresource.resource :as hal]
            [taoensso.timbre :as log]
            [liberator.representation :refer [ring-response render-map-generic]]
            [leaderboard_service.representation]
            [leaderboard_service.db :as db]))

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

(defn media-types
  "Returns the media-types declared to be available in the Swagger definition
  of a resource."
  [spec]
  (->> spec
       (vals)
       (mapcat #(find % "produces"))
       (flatten)
       (set)
       (filterv #(not= "produces" %))))

(defn describe-resource
  "Injects the Swagger specification for the given path into the response body."
  [ctx path spec]
  (let [rep (get-in ctx [:request :headers "accept"])
        ctx (assoc ctx :representation {:media-type rep})
        body (render-map-generic {path spec} ctx)]
    (ring-response {:body body})))
