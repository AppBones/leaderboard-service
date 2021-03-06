(ns leaderboard_service.util
  (:require [io.sarnowski.swagger1st.executor :as s1stexec]
            [clojure.string :as str]))

(defn split
  "clojure.string/split, but with nil punting."
  [s re]
  (if (nil? s)
    nil
    (str/split s re)))

(defn operationId->func
  "Given a swagger request definition, resolves the operationId to the
  matching liberator resource function.

  operationIds are expected to be of the form:

     namespace/resource*extraneous-information

  where extraneous-information is ignored."
  [request-definition]
  (let [opId (get request-definition "operationId")]
    (s1stexec/function-by-name (first (str/split opId #"\*")))))

(defn query-from-request
  [ctx]
  (get-in ctx [:request :parameters :query]))

(defn path-from-request
  [ctx]
  (get-in ctx [:request :parameters :path]))

(defn body-from-request
  [ctx]
  (get-in ctx [:request :parameters :body]))
