(ns planisphere.web.home
  (:require
   [planisphere.web.ws :refer [clj->transit]]
   [ring.util.response :refer [content-type]]
   [compojure.core :as comp :refer [defroutes GET POST]]
   [compojure.route :as route]
   [clojure.java.io :as io]
   [hiccup.core :refer [html]]
   [hiccup.element :refer [javascript-tag]]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [hiccup.page :refer [include-css include-js]]
   [ring.util.http-response :as response]))

(defn unique-id
  "Get a unique id for a session."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))

(defn login
  "Create a Ring session given a client request."
  [req]
  {:status 200
   :session (if (session-uid req)
              (:session req)
              (assoc (:session req) :uid (unique-id)))})

(defn center
  [el]
  [:div.pure-u-1.center el])

(defn home-page
  []
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css "/css/style.css")
     (include-css "/css/pure-min.css")
     (include-css "/css/grids-responsive-min.css")
     (include-css "/css/zenburn.min.css")]
    [:body
     [:div (center [:pre (slurp (io/resource "ascii.txt"))])
      [:div#app
       (center "loading")]]
     (include-js "/js/compiled/planisphere.js")
     (javascript-tag "window.onload=function(){planisphere.core.run();}")]]))

(defroutes home-routes
  (GET  "/" req (-> (login req)
                    (assoc :body (home-page))
                    (content-type "text/html")))
  (GET "/token" req (response/ok (clj->transit {:csrf-token *anti-forgery-token*})))
  (route/resources "/")
  (route/not-found "<h1>Nope</h1>"))
