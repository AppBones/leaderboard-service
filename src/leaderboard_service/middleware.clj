(ns leaderboard_service.middleware
  (:require [buddy.sign.jwe :as jwe]
            [ring.util.response :as resp]
            [clojure.string :refer [split]]))

(defn wrap-oauth [handler pubkey]
  "Execute the wrapped handler only if it contains a JWT verifiable with the
  given key. Otherwise, return a 401 Unauthorized. When the token is good, stash
  it under key :jwt in the request."
  (fn [request]
    (let [t (last (split (get-in request [:headers "Authorization"]) " "))]
      (if (nil? t)
        {:status 401 :body "Unauthorized. No authentication provided."}
        (let [jwt (jwe/decrypt t pubkey {:alg :rsa-oaep-256
                                         :enc :a128cbc-hs256})]
          (if (nil? jwt)
            {:status 401 :body "Unauthorized. Invalid signature."}
            (handler (assoc request :jwt jwt))))))))
