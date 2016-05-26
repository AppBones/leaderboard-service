(ns leaderboard-service.leaderboard_api_test
  (:require leaderboard-service.leaderboard
            leaderboard_service.api
            [environ.core :refer [env]])
  (:use
    [io.sarnowski.swagger1st.context :only [create-context]]
    [ring.mock.request :as mock]
    [clojure.data.json :as json :only [read-str]]
    [clojure.walk :only [keywordize-keys]]
    [midje.sweet :refer :all]))

; (def swagger (create-context :yaml-cp "leaderboard-service-api.yml" true))
; (def definition (get-in swagger [:definition "paths" "/"]))
; (def handler leaderboard_service.leaderboard/leaderboards)
; (def db {:conn (str (env :database-url) "?sslmode=require")})
;
;
; ; ### /leaderboards
;
;
; ; GET /leaderboards
; (let [request (-> (mock/request :get "http://localhost:3000/api/v1/leaderboards")
;                   (assoc :parameters {:query {:appId 1
;                                               :limit 10
;                                               :page 1}})
;                   (assoc-in [:headers "accept"] "application/hal+json"))
;       spec (get-in definition ["get"])
;       response (handler request db spec)
;       body (keywordize-keys (json/read-str (:body response)))]
;
;   (facts "about GET"
;          response => (contains {:status 200})
;          response => (contains {:headers {"Content-Type" "application/hal+json;charset=UTF-8", "Vary" "Accept"}})
;          body => {:_links {:self {:href "http://localhost:3000/api/v1/leaderboards"}},
;                   :leaderboards [{:app_id 1,
;                                   :description nil,
;                                   :id 2,
;                                   :max_score 2147483647,
;                                   :min_score -2147483648,
;                                   :score_type "int",
;                                   :sorting_order "descending",
;                                   :title "Ninja board"}
;                                  ]}))
;
;
; ; OPTIONS /leaderboards
; (let [request (-> (mock/request :options "http://localhost:3000/api/v1/leaderboards")
;                   (assoc-in [:swagger :context] swagger)
;                   (assoc-in [:headers "accept"] "application/hal+json"))
;       spec (get-in definition ["options"])
;       response (handler request db spec)
;       body (:body response)]
;   (facts "about OPTIONS"
;          response => (contains {:status 200})
;          response => (contains {:headers {"Allow" "GET, POST, OPTIONS"}})
;          body => (json/write-str {:leaderboards definition})))
;
;
; ; ### /leaderboard
;
; ; OPTIONS /leaderboards/{board_id}
; (let [request (-> (mock/request :options "http://localhost:3000/api/v1/leaderboards/2")
;                   (assoc-in [:swagger :context] swagger)
;                   (assoc-in [:headers "accept"] "application/hal+json"))
;       def (get-in swagger [:definition "paths" "/{board_id}"])
;       spec (get-in definition ["options"])
;       response (handler request db spec)
;       body (:body response)]
;   (facts "about OPTIONS"
;          response => (contains {:status 200})
;          response => (contains {:headers {"Allow" "GET, POST, OPTIONS"}})
;          body => (json/write-str {:leaderboards def})))
