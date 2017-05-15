(ns planisphere.web.browser
  (:require
   [clojure.string :refer [join]]
   [mount.core :as mount :refer [defstate]]
   [clojure.java.browse :refer [browse-url]]
   [sparkledriver.core :as headless :refer [close-browser!]]))

(defn url
  "Generate the home url given a map containing a :host and :port"
  [m]
  (let [hp ((juxt :host :port) m)]
    (format "http://%s/" (join ":" hp))))

(defn- browse-headless [url]
  (let [settings (-> (com.machinepublishers.jbrowserdriver.Settings/builder)
                     (.headScript nil)
                     .build)]
    (doto (com.machinepublishers.jbrowserdriver.JBrowserDriver. settings)
      .init
      (.get url))))

(defstate default-browser
  :start
  (browse-url (url (mount/args))))

(defstate headless-browser
  :start
  (browse-headless (url (mount/args)))
  :stop
  (close-browser! headless-browser))
