(ns planisphere.web.server
  (:require
   [planisphere.web.home :refer [home-routes]]
   [planisphere.web.ws :refer [websocket-routes
                               broadcast-log-level]]
   [compojure.core :refer [routes]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.timbre :as timbre :refer [infof]]
   [org.httpkit.server :refer [run-server]]
   [mount.core :as mount :refer [defstate args start-with-args stop]]))

(def app-routes
  (routes
   #'websocket-routes
   #'home-routes))

(def app
  (ring.middleware.defaults/wrap-defaults
   app-routes
   ring.middleware.defaults/site-defaults))

(defn set-client-level!
  "Change the log level of the client browser."
  [level]
  (broadcast-log-level level))

(defstate ^{:on-reload :noop}
  web-server
  :start
  (run-server app (mount/args))
  :stop
  (web-server))
